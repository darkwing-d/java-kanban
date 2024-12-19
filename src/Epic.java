import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task{
    protected ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description, TaskStatus status) {                                  //, String status
        super(name, description, status); // статус эпика при создании
    }

    public Epic() {
        super();
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public void cleanSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Epic epic = (Epic) object;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(subtaskIds);
    }

}