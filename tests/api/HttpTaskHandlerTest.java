package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
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

import static org.junit.jupiter.api.Assertions.*;


class HttpTaskHandlerTest {

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
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task(
                "Task 1",
                "DescriptionForTask_1",
                TaskStatus.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2026, 1, 10, 10, 5)
        );

        String taskJson = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/tasks");

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // статус ответа
        assertEquals(201, response.statusCode(), "Ожидался статус 201 (Created)");

        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");

        assertEquals("Task 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void shouldGetTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "DescriptionForTask_1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.of(2026, 1, 10, 10, 5));
        Task task2 = new Task("Task 2", "DescriptionForTask_2", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.of(2026, 1, 11, 10, 5));

        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());

        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        client.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseGet.statusCode(), "Ожидался статус 200 (OK)");

        List<Task> tasksFromManager = gson.fromJson(responseGet.body(), taskType);
        assertNotNull(tasksFromManager, "Список задач не должен быть null");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "DescriptionForTask_1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.of(2026, 1, 10, 10, 5));
        Task task2 = new Task("Task 2", "DescriptionForTask_2", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.of(2026, 1, 11, 10, 5));

        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);

        URI url = URI.create("http://localhost:8080/tasks");
        URI url2 = URI.create("http://localhost:8080/tasks/2");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        client.send(request1, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(url2)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseGet.statusCode(), "Ожидался статус 200 (OK)");

        Task responseTask = gson.fromJson(responseGet.body(), Task.class);
        assertEquals(task2.getName(), responseTask.getName(), "Заголовки задач не совпадают");
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "DescriptionForTask_1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.of(2026, 1, 10, 10, 5));
        Task task2 = new Task("Task 2", "DescriptionForTask_2", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.of(2026, 1, 11, 10, 5));

        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);

        URI url = URI.create("http://localhost:8080/tasks");
        URI url2 = URI.create("http://localhost:8080/tasks/2");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        client.send(request1, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());

        //  DELETE-запрос для удаления задачи по ID
        HttpRequest requestDelete = HttpRequest.newBuilder()
                .DELETE()
                .uri(url2)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDelete.statusCode(), "Ожидался статус 200 (OK)");

        assertNull(taskManager.getTask(2), "Задача с ID 2 должна быть отсутствовать");

        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(url2)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseGet.statusCode(), "Ожидался статус 404 (не найдено)");
    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "DescriptionForTask_1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.of(2026, 1, 10, 10, 5));
        Task task2 = new Task("Task 2", "DescriptionForTask_2", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.of(2026, 1, 11, 10, 5));

        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);

        URI url = URI.create("http://localhost:8080/tasks");
        URI url2 = URI.create("http://localhost:8080/tasks/2");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        client.send(request1, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(url2)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        Task responseTask = gson.fromJson(responseGet.body(), Task.class);
        responseTask.setStatus(TaskStatus.DONE);
        String responseTaskToJson = gson.toJson(responseTask);

        HttpRequest requestUpdateTask = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(responseTaskToJson))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> responseUpdateTask = client.send(requestUpdateTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseUpdateTask.statusCode(), "Ожидался статус 200 (OK) при обновлении задачи");

        HttpResponse<String> responseAfterUpdate = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAfterUpdate.statusCode(), "Ожидался статус 200 (OK) при получении обновленной задачи");

        Task responseTaskAfterUpdate = gson.fromJson(responseAfterUpdate.body(), Task.class);

        // статус обновленной задачи соответствует ожидаемому
        assertEquals(responseTask.getStatus(), responseTaskAfterUpdate.getStatus(), "Статусы задач не совпадают");
    }

    @Test
    void shouldCreateOverlappingTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "DescriptionForTask_1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.of(2026, 1, 11, 10, 5));
        Task task2 = new Task("Task 2", "DescriptionForTask_2", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.of(2026, 1, 11, 10, 5));

        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .uri(url)
                .header("Accept", "application/json")
                .build();

        // создание второй задачи с конфликтующим временем
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .uri(url)
                .header("Accept", "application/json")
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response1 = client.send(request1, handler);
        HttpResponse<String> response2 = client.send(request2, handler);

        // проверка кодов статусов для первого и второго запросов
        assertEquals(201, response1.statusCode(), "Ожидался статус 201 при создании первой задачи.");
        assertEquals(406, response2.statusCode(), "Ожидался статус 406 при создании перекрывающей задачи.");
    }
}