package api.handlers;

import api.DurationAdapter;
import api.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {
    private String response;

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response = "";
        int statusCode = 200;

        try {
            switch (method) {
                case "GET":
                    String path = exchange.getRequestURI().getPath();

                    if (path.equals("/epics")) {
                        // получение списка всех эпиков
                        List<Epic> epics = taskManager.getEpics();
                        response = gson.toJson(epics);
                    } else if (path.matches("/epics/\\d+")) {
                        // получение эпика по ID
                        int id = Integer.parseInt(path.split("/")[2]);
                        Epic epic = taskManager.getEpic(id);
                        if (epic != null) {
                            response = gson.toJson(epic);
                        } else {
                            statusCode = 404;
                            response = "Эпик с ID " + id + " не найден";
                        }
                    } else {
                        statusCode = 400;
                        response = "Неправильный путь запроса";
                    }
                    break;

                case "POST":
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                            .registerTypeAdapter(Duration.class, new DurationAdapter())
                            .create();
                    Epic epic = gson.fromJson(body, Epic.class);

                    taskManager.addNewEpic(epic);

                    statusCode = 201;
                    response = "Эпик успешно создан";
                    break;

                case "DELETE":
                    String delPath = exchange.getRequestURI().getPath();
                    String idStr = delPath.substring(delPath.lastIndexOf("/") + 1);
                    int id = Integer.parseInt(idStr);
                    boolean deleted = taskManager.deleteEpic(id);

                    if (deleted) {
                        statusCode = 204; // No Content
                        response = "";
                    } else {
                        statusCode = 404; // Not Found
                        response = "Эпик не найден";
                    }
                    break;

                default:
                    statusCode = 405;
                    response = "Метод не поддерживается";
                    break;
            }
        } catch (Exception e) {
            statusCode = 500;
            response = "Ошибка на сервере: " + e.getMessage();
        } finally {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    @Override
    protected void getHandle(HttpExchange httpExchange, String[] path) throws IOException {
        if (path.length == 2) {
            handleGetAllEpics(httpExchange);
        } else if (path.length == 3) {
            handleGetEpicById(httpExchange, path[2]);
        } else if (path.length == 4 && "subtasks".equals(path[3])) {
            handleGetSubtasksOfEpic(httpExchange, path[2]);
        } else {
            sendNotFound(httpExchange);
        }
    }

    private void handleGetAllEpics(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(taskManager.getEpics());
        sendText(httpExchange, response, 200);
    }

    private void handleGetEpicById(HttpExchange httpExchange, String idString) throws IOException {
        try {
            int id = Integer.parseInt(idString);
            Epic epic = taskManager.getEpic(id);
            if (epic != null) {
                response = gson.toJson(epic);
                sendText(httpExchange, response, 200);
            } else {
                sendNotFound(httpExchange);
            }
        } catch (NumberFormatException e) {
            sendNotFound(httpExchange);
        }
    }

    private void handleGetSubtasksOfEpic(HttpExchange httpExchange, String idString) throws IOException {
        try {
            int id = Integer.parseInt(idString);
            Epic epic = taskManager.getEpic(id);
            if (epic != null) {
                response = gson.toJson(taskManager.getSubtasksForEpic(id));
                sendText(httpExchange, response, 200);
            } else {
                sendNotFound(httpExchange);
            }
        } catch (NumberFormatException e) {
            sendNotFound(httpExchange);
        }
    }

    @Override
    protected void postHandle(HttpExchange httpExchange) throws IOException {
        String bodyRequest = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (bodyRequest.isEmpty()) {
            sendBadRequest(httpExchange);
            return;
        }

        try {
            Epic epic = gson.fromJson(bodyRequest, Epic.class);
            taskManager.addNewEpic(epic);
            sendText(httpExchange, "Success", 201);
        } catch (JsonSyntaxException e) {
            sendBadRequest(httpExchange);
        }
    }

    @Override
    protected void deleteHandle(HttpExchange httpExchange, String[] path) throws IOException {
        if (path.length != 3) {
            sendNotFound(httpExchange);
            return;
        }

        try {
            int id = Integer.parseInt(path[2]);
            taskManager.deleteEpic(id);
            sendText(httpExchange, "Success", 200);
        } catch (NumberFormatException e) {
            sendNotFound(httpExchange);
        }
    }

    private void sendBadRequest(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(400, -1); // 400 Bad Request
    }
}
