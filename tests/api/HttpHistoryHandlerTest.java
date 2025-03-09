package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpHistoryHandlerTest {
    private TaskManager taskManager = new InMemoryTaskManager();
    private HttpTaskServer taskServer;
    private Gson gson = Managers.getGson();
    private Type taskType = new TypeToken<List<Task>>() {
    }.getType();

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    void shouldGetHistory() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "DescriptionForEpic_1");

        Subtask subtask11 = new Subtask("Subtask 1", "DescriptionForSubtask_1", TaskStatus.DONE,
                epic.getId(), Duration.ZERO, LocalDateTime.now());
        subtask11.setStartTime(LocalDateTime.of(2025, 2, 11, 12, 0));
        subtask11.setDuration(Duration.ofHours(3));

        Subtask subtask12 = new Subtask("Subtask 2", "DescriptionForSubtask_2", TaskStatus.IN_PROGRESS,
                epic.getId(), Duration.ZERO, LocalDateTime.now());
        subtask12.setStartTime(LocalDateTime.of(2025, 2, 11, 16, 0));
        subtask12.setDuration(Duration.ofHours(2));

        URI url = URI.create("http://localhost:8080/history");

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());

        List<Task> history = gson.fromJson(response.body(), taskType);
        assertNotNull(history, "История не возвращается");
    }
}
