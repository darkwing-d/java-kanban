package api;

import api.handlers.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private final HttpServer httpServer;
    private static final int PORT = 8080;


    public HttpTaskServer(TaskManager taskManager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public void start() {
        httpServer.setExecutor(null);
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static void main(String[] args) {
        // экземпляр Gson с зарегистрированными адаптерами
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);

        String jsonLocalDateTime = gson.toJson(now);
        String jsonDuration = gson.toJson(duration);

        System.out.println("Сериализованное LocalDateTime: " + jsonLocalDateTime);
        System.out.println("Сериализованная Duration (в секундах): " + jsonDuration); // в секундах

        LocalDateTime deserializedDateTime = gson.fromJson(jsonLocalDateTime, LocalDateTime.class);
        Duration deserializedDuration = gson.fromJson(jsonDuration, Duration.class);

        System.out.println("Десериализованное LocalDateTime: " + deserializedDateTime);
        System.out.println("Десериализованная Duration: " + deserializedDuration); // в формате Duration
    }
}

