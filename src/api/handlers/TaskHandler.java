package api.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.*;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void getHandle(HttpExchange httpExchange, String[] path) throws IOException {
        String response;

        // проверка, что в пути достаточно элементов
        if (path.length == 2) {
            response = gson.toJson(taskManager.getTasks());
            sendText(httpExchange, response, 200);
        } else {
            try {
                int id = Integer.parseInt(path[2]);
                Task task = taskManager.getTask(id);

                if (task != null) {
                    response = gson.toJson(task);
                    sendText(httpExchange, response, 200);
                } else {
                    sendNotFound(httpExchange);
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                sendNotFound(httpExchange);
            }
        }
    }

    @Override
    protected void postHandle(HttpExchange httpExchange) throws IOException {
        String bodyRequest = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (bodyRequest.isEmpty()) {
            sendNotFound(httpExchange);
            return;
        }

        try {
            Task task = gson.fromJson(bodyRequest, Task.class);

            // перекрытие времени с существующими задачами
            for (Task existingTask : taskManager.getTasks()) {
                if (existingTask.getId() != task.getId() &&
                        existingTask.getStartTime().isBefore(task.getEndTime()) &&
                        existingTask.getEndTime().isAfter(task.getStartTime())) {
                    // если перекрытие, статус 406
                    sendText(httpExchange, "Task time overlaps with an existing task", 406);
                    return;
                }
            }

            if (taskManager.getTask(task.getId()) != null) {
                taskManager.updateTask(task);
                sendText(httpExchange, "Task updated successfully", 200);
            } else {
                taskManager.addNewTask(task);
                sendText(httpExchange, "Task added successfully", 201);
            }
        } catch (JsonSyntaxException e) {
            sendText(httpExchange, "Invalid task data", 400); // Неверные данные
        } catch (Exception e) {
            sendText(httpExchange, "An unexpected error occurred", 500);
        }
    }

    @Override
    protected void deleteHandle(HttpExchange httpExchange, String[] path) throws IOException {
        if (path.length < 3) {
            sendNotFound(httpExchange);
            return;
        }

        try {
            int id = Integer.parseInt(path[2]);
            if (taskManager.getTask(id) != null) {
                taskManager.deleteTask(id);
                sendText(httpExchange, "Task deleted successfully", 200);
            } else {
                sendNotFound(httpExchange);
            }
        } catch (NumberFormatException e) {
            sendNotFound(httpExchange);
        } catch (Exception e) {
            sendText(httpExchange, "An unexpected error occurred", 500);
        }
    }
}
