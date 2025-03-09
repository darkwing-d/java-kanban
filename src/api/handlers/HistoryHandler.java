package api.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler{

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String[] path = httpExchange.getRequestURI().getPath().split("/");

        if ("GET".equalsIgnoreCase(method)) {
            getHandle(httpExchange, path);
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }

    @Override
    protected void getHandle(HttpExchange httpExchange, String[] path) throws IOException {
        try {
            String response = gson.toJson(taskManager.getHistory());
            sendText(httpExchange, response, 200);
        } catch (Exception e) {
            sendInternalServerError(httpExchange);
        }
    }

    private void sendMethodNotAllowed(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
    }

    private void sendInternalServerError(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(500, -1); // 500 Internal Server Error
    }
}
