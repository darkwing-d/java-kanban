import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TasksManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int generatorId = 0;

    public ArrayList<Task> getTasks() {
        ArrayList<Task> taskArrayList = new ArrayList<>(tasks.values());
        return taskArrayList;
    }

    public int addNewTask(Task task) {
        final int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public int addNewEpic(Epic epic) {
        final int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    public Integer addNewSubtask(Subtask subtask) {
        if (subtask == null || !epics.containsKey(subtask.getEpicId())) {
            return null; // проверка что эпик существует
        }

        final int id = ++generatorId;
        subtask.setId(id);
        subtasks.put(id, subtask);
        epics.get(subtask.getEpicId()).addSubtaskId(id);
        updateStatus(subtask.getEpicId());
        return id;
    }

    public boolean removeSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask != null) {
            int epicId = subtask.getEpicId(); // id эпика из подзадач
            Epic epic = epics.get(epicId);

            if (epic != null) {
                epic.removeSubtaskId(subtaskId); // уделание id подзадачи из эпика
                updateStatus(epicId);
            }

            return true;
        }
        return false;
    }

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

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicArrayList = new ArrayList<>(epics.values());
        return epicArrayList;
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> subtaskArrayList = new ArrayList<>(subtasks.values());
        return subtaskArrayList;
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public boolean updateTask(Task task) {
        int id = task.getId();
        // помещение задачи в коллекцию
        if (tasks.containsKey(id)) {
            tasks.put(id, task); // обновление в hashMap
            return true;
        }
        return false;
    }

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

    public boolean deleteTask(int id) {
        return tasks.remove(id) != null;
    }

    public void deleteSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId); // получение эпика

            if (epic != null) {
                epic.removeSubtaskId(subtaskId);
                updateStatus(epicId);
            }
        }
    }

    private void updateStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            TaskStatus newStatus = calculateStatus(); // Рассчитываем новый статус
            epic.setStatus(newStatus); // Обновляем статус эпика
        }
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
}














