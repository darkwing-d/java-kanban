/*
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public interface ITaskManager {

    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getEpicSubtasks(int epicId);

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    default Integer addNewSubtask(Subtask subtask) {
        Epic epic = getEpic(subtask.getEpicId());
        if (epic == null) {
            System.out.println("no such epic:" + subtask.getEpicId());
            return -1;
        }
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic);
        return -1;
    }

    static void updateEpicStatus(Epic epic) {
        Set<String> status = Collections.singleton(epic.getStatus()); //get all status
        if (status.isEmpty()) {
            epic.setStatus("NEW");
            return;
        }
        if (status.equals("IN_PROGRESS")) {
            epic.setStatus("IN_PROGRESS");
            return;
        }
        if (status.equals("DONE")) {
            epic.setStatus("DONE");
        }
    }


    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    void deleteTasks();

    void deleteSubtasks();

    void deleteEpics();

 /*   default void updateEpicsStatus(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subs = epic.getSubtaskIds();
        if (subs.isEmpty()) {
            epic.setStatus("NEW");
            return;
        }
        String status = null;
        for (int id : subs) {
            final Subtask subtask = subtasks.get(id);
            if (status == null) {
                status = subtask.getStatus();
                continue;
            }

            if (status.equals(subtask.getStatus()) && !status.equals("IN_PROGRESS")) {
                continue;
            }
            epic.setStatus("IN_PROGRESS");
            return;
        }
    }*/
















