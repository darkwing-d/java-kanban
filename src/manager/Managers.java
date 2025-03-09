package manager;

import api.DurationAdapter;
import api.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {

    private static final TaskManager taskManagerInstance = new InMemoryTaskManager();
    private static final HistoryManager historyManagerInstance = new InMemoryHistoryManager();
    private static final DurationAdapter durationAdapter = new DurationAdapter();
    private static final LocalDateTimeAdapter localDateTimeAdapter = new LocalDateTimeAdapter();

    public static TaskManager getDefault() {
        return taskManagerInstance;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManagerInstance;
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Duration.class, durationAdapter)
                .registerTypeAdapter(LocalDateTime.class, localDateTimeAdapter)
                .create();
    }

    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()) // Регистрируем адаптер для LocalDateTime
                .registerTypeAdapter(Duration.class, new DurationAdapter())         // Регистрируем адаптер для Duration
                .create();
    }
}