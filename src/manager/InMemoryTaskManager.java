package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;
    private int generatorId = 0;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    protected int generateId() {
        return ++generatorId;
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
        final int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        historyManager.add(task);
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
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateStatus(epic.getId());
        }
        subtasks.clear();
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
            tasks.put(id, task); // обновление в hashMap
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
            }
            return true;
        }
        return false;
    }

    public boolean deleteTask(int taskId) {
        Task task = tasks.remove(taskId); // Удаление из основной коллекции задач
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
}