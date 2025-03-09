package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

class HttpPrioritizedHandlerTest {

    private InMemoryTaskManager taskManager = new InMemoryTaskManager();
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
    void testGetPrioritizedTasks() {
        Task task1 = new Task("Task 1", "DescriptionForTask_1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.of(2025, 1, 10, 10, 5));
        Task task2 = new Task("Task 2", "DescriptionForTask_2", TaskStatus.IN_PROGRESS,
                Duration.ofHours(1), LocalDateTime.of(2025, 1, 9, 13, 0));
        Task task3 = new Task("Task 3", "DescriptionForTask_3", TaskStatus.IN_PROGRESS,
                Duration.ofHours(1), LocalDateTime.of(2025, 1, 11, 13, 0));

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(3, prioritizedTasks.size(), "Неверное количество задач");
        assertEquals(task2.getDescription(), prioritizedTasks.get(0).getDescription(), "Задачи не совпадают");
    }

    @Test
    void shouldGetPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "DescriptionForTask_1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.of(2025, 1, 10, 10, 5));
        Task task2 = new Task("Task 2", "DescriptionForTask_2", TaskStatus.IN_PROGRESS,
                Duration.ofHours(1), LocalDateTime.of(2025, 1, 9, 13, 0));
        Task task3 = new Task("Task 3", "DescriptionForTask_3", TaskStatus.IN_PROGRESS,
                Duration.ofHours(1), LocalDateTime.of(2025, 1, 11, 13, 0));

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        System.out.println("Ответ сервера: " + response.body());
        System.out.println("Статус ответа: " + response.statusCode());

        assertEquals(200, response.statusCode(), "Статус ответа должен быть 200");

        // десериализация ответа в список задач
        List<Task> prioritized = gson.fromJson(response.body(), taskType);
        assertNotNull(prioritized, "Список приоритетных задач не возвращается");

        assertEquals(3, prioritized.size(), "Неверное количество задач");

        assertEquals(task2.getDescription(), prioritized.get(0).getDescription(), "Задачи не совпадают");
    }
}
