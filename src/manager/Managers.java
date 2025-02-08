package manager;

public class Managers {

    private static final TaskManager taskManagerInstance = new InMemoryTaskManager();
    private static final HistoryManager historyManagerInstance = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return taskManagerInstance;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManagerInstance;
    }
}