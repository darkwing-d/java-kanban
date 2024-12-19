import java.util.Objects;

public class Subtask extends Task {
    protected int epicId;
    TasksManager tasksManager = new TasksManager();

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId) {
        super(name,description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setStatus(TaskStatus status) {
        super.status = status; // Обновляем статус подзадачи
        tasksManager.updateStatus(epicId);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Subtask subtask = (Subtask) object;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(epicId);
    }
}