package api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler implements HttpHandler {
    protected TaskManager taskManager;
    protected Gson gson = Managers.getGson();

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void handle(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");

        switch (exchange.getRequestMethod()) {
            case "GET":
                getHandle(exchange, path);
                break;
            case "POST":
                postHandle(exchange);
                break;
            case "DELETE":
                deleteHandle(exchange, path);
                break;
            default:
                sendNotFound(exchange);
                break;
        }
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, text.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(text.getBytes(StandardCharsets.UTF_8));
        }
    }

    protected void sendNotFound(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, "Not Found", 404);
    }

    protected void sendHasInteractions(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, "Not Acceptable", 406);
    }

    protected void getHandle(HttpExchange httpExchange, String[] path) throws IOException {
    }

    protected void postHandle(HttpExchange exchange) throws IOException {
    }

    protected void deleteHandle(HttpExchange exchange, String[] path) throws IOException {
    }
}