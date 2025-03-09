package manager;

import exceptions.TimeException;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;
    private int generatorId = 0;

    protected Comparator<Task> comparator = (t1, t2) -> {
        if (t1.getStartTime().isBefore(t2.getStartTime())) {
            return -1;
        } else if (t1.getStartTime().isAfter(t2.getStartTime())) {
            return 1;
        } else {
            return 0;
        }
    };

    protected final Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    protected int generateId() {
        return ++generatorId;
    }

    public List<Task> getPrioritizedTasks() {
        return tasks.values().stream()
                .sorted(Comparator.comparing(Task::getStartTime))
                .collect(Collectors.toList());
    }

    public TaskStatus getEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Epic not found");
        }

        List<Integer> epicSubtaskIds = epic.getSubtaskIds();
        if (epicSubtaskIds.isEmpty()) {
            return TaskStatus.NEW; // Эпик без подзадач
        }

        boolean allNew = true;
        boolean allDone = true;
        boolean hasInProgress = false;

        for (int subtaskId : epicSubtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                switch (subtask.getStatus()) {
                    case NEW:
                        allDone = false;
                        break;
                    case DONE:
                        allNew = false;
                        break;
                    case IN_PROGRESS:
                        allNew = false;
                        allDone = false;
                        hasInProgress = true;
                        break;
                }
            }
        }

        if (allNew) {
            return TaskStatus.NEW;
        } else if (allDone) {
            return TaskStatus.DONE;
        } else if (hasInProgress) {
            return TaskStatus.IN_PROGRESS;
        } else {
            return TaskStatus.IN_PROGRESS; // Вернем IN_PROGRESS, если есть смешанные статусы
        }
    }

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    public int getGeneratorId() {
        return generatorId;
    }

    @Override
    public List<Task> getTasks() {
        List<Task> taskArrayList = new ArrayList<>(tasks.values());
        return taskArrayList;
    }

    @Override
    public int addNewTask(Task task) {
        if (crossingInTime(task)) {
            throw new TimeException("Пересечение по времени с другой задачей.");
        }

        final int id = generateId();
        task.setId(id);

        tasks.put(id, task);
        historyManager.add(task);
        prioritizedTasks.add(task);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        final int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        if (subtask == null || !epics.containsKey(subtask.getEpicId())) {
            return null; // проверка что эпик существует
        }

        Epic epic = epics.get(subtask.getEpicId());
        if (epic.getId() == subtask.getId()) {
            return null;
        }

        final int id = generateId();
        subtask.setId(id);
        subtasks.put(id, subtask);

        epics.get(subtask.getEpicId()).addSubtaskId(id);
        updateStatus(subtask.getEpicId());
        epics.get(subtask.getEpicId()).updateCalculatedFields(this);
        if (crossingInTime(subtask)) {
            throw new TimeException("Пересечение по времени с другой задачей.");
        } else {
            prioritizedTasks.add(subtask);
        }
        return id;
    }

    @Override
    public boolean removeSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask != null) {
            int epicId = subtask.getEpicId(); // id эпика из подзадач
            Epic epic = epics.get(epicId);

            if (epic != null) {
                epic.removeSubtaskId(Integer.valueOf(subtaskId));
                updateStatus(epicId);
            }

            return true;
        }
        return false;
    }

    @Override
    public List<Subtask> getSubtasksForEpic(int epicId) {
        List<Subtask> result = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                result.add(subtasks.get(subtaskId));
            }
        }
        return result;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> epicArrayList = new ArrayList<>(epics.values());
        return epicArrayList;
    }

    public List<Subtask> getSubtasks() {
        List<Subtask> subtaskArrayList = new ArrayList<>(subtasks.values());
        return subtaskArrayList;
    }

    @Override
    public void deleteTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                historyManager.remove(subtaskId);
                prioritizedTasks.remove(subtasks.get(subtaskId));
                subtasks.remove(subtaskId);
            }
            updateStatus(epic.getId());
            epic.getSubtaskIds().clear();
        }
    }

    @Override
    public Task getTask(int id) {
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public List<Task> getHistory() {
        return historyManager.getHistory(); // Получаем историю из historyManager
    }

    @Override
    public boolean updateTask(Task task) {
        int id = task.getId();
        // помещение задачи в коллекцию
        if (tasks.containsKey(id)) {
            Task oldTask = tasks.get(task.getId());
            prioritizedTasks.remove(oldTask);
            tasks.put(id, task); // обновление в hashMap
            if (crossingInTime(task)) {
                throw new TimeException("Пересечение по времени с другой задачей.");
            } else {
                prioritizedTasks.add(task);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean updateEpic(Epic epicToUpdate) {
        int id = epicToUpdate.getId();
        Epic existingEpic = epics.get(id);
        if (existingEpic != null) {
            existingEpic.setName(epicToUpdate.getName());
            existingEpic.setDescription(epicToUpdate.getDescription());
            // далее можно дописывать добавление новых свойств
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSubtask(Subtask updatedSubtask) {
        if (updatedSubtask == null) {
            return false;
        }

        if (crossingInTime(updatedSubtask)) {
            throw new TimeException("Наложение по времени с другой задачей.");
        } else {
            prioritizedTasks.add(updatedSubtask);
        }

        Subtask existingSubtask = subtasks.get(updatedSubtask.getId());
        if (existingSubtask != null) {
            if (updatedSubtask.getName() != null) {
                existingSubtask.setName(updatedSubtask.getName());
            }
            if (updatedSubtask.getDescription() != null) {
                existingSubtask.setDescription(updatedSubtask.getDescription());
            }
            if (updatedSubtask.getStatus() != null) {
                existingSubtask.setStatus(updatedSubtask.getStatus());
            }

            Epic epic = epics.get(existingSubtask.getEpicId());
            if (epic != null) {
                updateStatus(epic.getId());
            }


            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEpic(int id) {
        // Получаем эпик
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                prioritizedTasks.remove(subtasks.get(subtaskId));
            }
            return true;
        }
        return false;
    }

    public boolean deleteTask(int taskId) {
        Task task = tasks.remove(taskId); // Удаление из основной коллекции задач
        prioritizedTasks.remove(task);
        if (task != null) {
            historyManager.remove(taskId); // Удаление из истории
        }
        return task != null; // Если задача была найдена и удалена
    }

    @Override
    public boolean deleteSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId); // получение эпика
            prioritizedTasks.remove(subtask);

            if (epic != null) {
                epic.removeSubtaskId(subtaskId);
                updateStatus(epicId);
            }
        }
        return subtask != null;
    }

    public boolean taskExists(int id) {
        return tasks.containsKey(id);
    }

    private TaskStatus calculateStatus() {
        if (subtasks.isEmpty()) {
            return TaskStatus.NEW; // Если нет подзадач, статус NEW
        }

        boolean allDone = true;
        boolean anyInProgress = false;

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStatus() == null) {
                continue;
            }
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                anyInProgress = true;
            } else if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }


        if (allDone) {
            return TaskStatus.DONE;
        } else if (anyInProgress) {
            return TaskStatus.IN_PROGRESS;
        } else {
            return TaskStatus.NEW;
        }
    }

    private void updateStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            TaskStatus newStatus = calculateStatus(); // Рассчитываем новый статус
            epic.setStatus(newStatus); // Обновляем статус эпика
        }
    }

    //метод для определения пересечения по времени
    private boolean crossingInTime(Task newTask) {
        for (Task existingTask : tasks.values()) {
            if (existingTask.getId() != newTask.getId() && overlaps(existingTask, newTask)) {
                return true;
            }
        }
        return false;
    }

    private boolean overlaps(Task task1, Task task2) {
        if (task1 == null || task2 == null || task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = start1.plus(task1.getEpicDuration());

        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = start2.plus(task2.getEpicDuration());

        return (start1.isBefore(end2) && start2.isBefore(end1));
    }
}