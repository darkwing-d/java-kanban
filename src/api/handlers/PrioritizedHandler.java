package api.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    private InMemoryTaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
        this.taskManager = (InMemoryTaskManager) taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            if ("GET".equals(method)) {
                List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                String response = gson.toJson(prioritizedTasks);
                sendText(exchange, response, 200);
            } else {
                sendMethodNotAllowed(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalServerError(exchange);
        }
    }

    @Override
    protected void getHandle(HttpExchange exchange, String[] path) throws IOException {
        try {
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            String response = gson.toJson(prioritizedTasks);
            sendText(exchange, response, 200);
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    private void sendMethodNotAllowed(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
    }

    private void sendInternalServerError(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(500, -1); // 500 Internal Server Error
    }
}
