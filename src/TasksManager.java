import java.util.ArrayList;
import java.util.HashMap;

public class TasksManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int generatorId = 0;

    //   @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> taskArrayList = new ArrayList<>(tasks.values());
        return taskArrayList;
    }

    //  @Override
    public Epic getEpic(int id) {
        return epics.get(id);
    }

    //  @Override
    public int addNewTask(Task task) {
        final int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    //  @Override
    public int addNewEpic(Epic epic) {
        final int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        updateStatus(id);
        return id;
    }

    //  @Override
    public Integer addNewSubtask(Subtask subtask) {
        final int id = ++generatorId;
        subtask.setId(id);
        subtasks.put(id, subtask);
        updateStatus(id);
        return id;
    }

    //   @Override
    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicArrayList = new ArrayList<>(epics.values());
        return epicArrayList;
    }

    public ArrayList<Subtask> getSubtasks(int id) {
        ArrayList<Subtask> subtaskArrayList = new ArrayList<>(subtasks.values());
        return subtaskArrayList;
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        Epic epic = new Epic();
        epic.subtaskIds.clear();
    }

    public Task searchIdTasks(int id) {
        return tasks.get(id);
    }

    public Epic searchIdEpics(int id) {
        return epics.get(id);
    }

    public Subtask searchIdSubtasks(int id) {
        return subtasks.get(id);
    }

    public boolean updateTask(int id, String newName, String newDescription) {
        Task task = tasks.get(id);
        if (task != null) {
            task.setName(newName);
            task.setDescription(newDescription);
            return true;
        }
        return false;
    }

    public boolean updateEpic(int id, String newName, String newDescription) {
        Epic epic = epics.get(id);
        if (epic != null) {
            epic.setName(newName);
            epic.setDescription(newDescription);
            return true;
        }
        return false;
    }

    public boolean updateSubtask(int id, String newName, String newDescription) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            subtask.setName(newName);
            subtask.setDescription(newDescription);
            return true;
        }
        return false;
    }

    public boolean deleteIdEpic(int id) {
        // Получаем эпик
        Epic epic = epics.remove(id);
        if (epic != null) {
            // Удаляем все подзадачи, связанные с данным эпиком
            subtasks.values().removeIf(subtask -> subtask.getId() == id);
            return true;
        }
        return false;
    }

    public boolean deleteIdTask(int id) {
        return tasks.remove(id) != null;
    }

    public void deleteIdSubtask(int id) {
        subtasks.remove(id);
    }

    public ArrayList<Subtask> getEpicsSubtasks(int id) {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == id) {
                result.add(subtask);
            }
        }
        return result;
    }

    public void updateStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            TaskStatus newStatus = calculateStatus(); // Рассчитываем новый статус
            epic.setStatus(newStatus); // Обновляем статус эпика
        }
    }

    public TaskStatus calculateStatus() {
        if (subtasks.isEmpty()) {
            return TaskStatus.NEW; // Если нет подзадач, статус NEW
        }

        boolean allDone = true;
        boolean anyInProgress = false;

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStatus() == null) {
                // Возможно назначить статус по умолчанию или пропустить
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

    public void deleteAllTasks() {
        deleteTasks();
        deleteEpics();
    }

    public int getTaskCount() {
        return tasks.size();
    }

    public int getEpicCount() {
        return epics.size();
    }
}














