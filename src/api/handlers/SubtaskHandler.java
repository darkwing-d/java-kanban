package api.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerCrossingTimeException;
import manager.TaskManager;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void getHandle(HttpExchange httpExchange, String[] path) throws IOException {
        String response;

        if (path.length == 2) {
            response = gson.toJson(taskManager.getSubtasks());
            sendText(httpExchange, response, 200);
        } else {
            try {
                int id = Integer.parseInt(path[2]);
                Task task = taskManager.getSubtask(id);

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
            Subtask subtask = gson.fromJson(bodyRequest, Subtask.class);

            if (taskManager.getTask(subtask.getId()) != null) {
                taskManager.updateSubtask(subtask);
                sendText(httpExchange, "Subtask updated successfully", 200);
            } else {
                taskManager.addNewSubtask(subtask);
                sendText(httpExchange, "Subtask added successfully", 201);
            }
        } catch (ManagerCrossingTimeException e) {
            sendHasInteractions(httpExchange);
        } catch (JsonSyntaxException e) {
            sendNotFound(httpExchange);
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
            if (taskManager.getSubtask(id) != null) {
                taskManager.deleteSubtask(id);
                sendText(httpExchange, "Subtask deleted successfully", 200);
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
