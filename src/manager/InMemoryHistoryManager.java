package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyTasks = new ArrayList<>();
    private int historyId = 0;

    @Override
    public void add(Task task) {
        if (historyId < 10) {
            historyTasks.add(historyId, task);
            historyId++;
        } else if (historyId >= 10) {
            historyId = 0;
            historyTasks.add(historyId, task);
            historyId++;
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyTasks);
    }
}
