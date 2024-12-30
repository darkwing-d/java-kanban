package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    Integer addNewSubtask(Subtask subtask);

    boolean removeSubtask(int subtaskId);

    List<Subtask> getSubtasksForEpic(int epicId);

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    boolean updateTask(Task task);

    boolean updateEpic(Epic epicToUpdate);

    boolean updateSubtask(Subtask updatedSubtask);

    boolean deleteEpic(int id);

    boolean deleteTask(int id);

    void deleteSubtask(int subtaskId);

    List<Task> getHistory();

}
