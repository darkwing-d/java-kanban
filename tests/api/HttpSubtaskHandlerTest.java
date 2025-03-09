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

import static org.junit.jupiter.api.Assertions.*;

public class HttpSubtaskHandlerTest {

    private TaskManager taskManager = new InMemoryTaskManager();
    private HttpTaskServer taskServer;
    private Gson gson = Managers.getGson();
    private Type subtaskType = new TypeToken<List<Subtask>>() {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        taskManager.addNewEpic(new Epic("Epic 1", "DescriptionForEpic_1"));

        Subtask subtask1 = new Subtask("Subtask 1", "DescriptionForSubtask_1", TaskStatus.DONE, 1,
                Duration.ZERO, LocalDateTime.now());
        subtask1.setStartTime(LocalDateTime.of(2025, 2, 11, 12, 0));
        subtask1.setDuration(Duration.ofHours(3));

        Subtask subtask2 = new Subtask("Subtask 2", "DescriptionForSubtask_2", TaskStatus.IN_PROGRESS, 1,
                Duration.ZERO, LocalDateTime.now());
        subtask2.setStartTime(LocalDateTime.of(2025, 2, 11, 16, 0));
        subtask2.setDuration(Duration.ofHours(2));

        String subtaskJson1 = gson.toJson(subtask1);
        String subtaskJson2 = gson.toJson(subtask2);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson1))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json") // Указание типа содержимого
                .header("Accept", "application/json")
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson2))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json") // Указание типа содержимого
                .header("Accept", "application/json")
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response1 = client.send(request1, handler);
        HttpResponse<String> response2 = client.send(request2, handler);

        assertEquals(201, response2.statusCode(), "Статус ответа должен быть 201");

        List<Subtask> subtasksFromManager = taskManager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(2, subtasksFromManager.size(), "Некорректное количество задач");

        // проверка имен подзадач
        assertEquals("Subtask 1", subtasksFromManager.get(0).getName(), "Некорректное имя первой задачи");
        assertEquals("Subtask 2", subtasksFromManager.get(1).getName(), "Некорректное имя второй задачи");
    }

    @Test
    void shouldGetSubtasks() throws IOException, InterruptedException {
        taskManager.addNewEpic(new Epic("Epic 1", "DescriptionForEpic_1"));

        Subtask subtask1 = new Subtask("Subtask 1", "DescriptionForSubtask_1", TaskStatus.DONE, 1,
                Duration.ZERO, LocalDateTime.now());
        subtask1.setStartTime(LocalDateTime.of(2025, 2, 11, 12, 0));
        subtask1.setDuration(Duration.ofHours(3));

        Subtask subtask2 = new Subtask("Subtask 2", "DescriptionForSubtask_2", TaskStatus.IN_PROGRESS, 1,
                Duration.ZERO, LocalDateTime.now());
        subtask2.setStartTime(LocalDateTime.of(2025, 2, 11, 16, 0));
        subtask2.setDuration(Duration.ofHours(2));

        String subtaskJson1 = gson.toJson(subtask1);
        String subtaskJson2 = gson.toJson(subtask2);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson1))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json") // тип содержимого
                .header("Accept", "application/json")
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson2))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        client.send(request1, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        // GET запрос и ответ
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        // проверка ответа
        assertEquals(200, responseGet.statusCode(), "Статус ответа должен быть 200");

        // десериализуем ответ в список подзадач
        List<Subtask> subtasksFromManager = gson.fromJson(responseGet.body(), subtaskType);

        // список не равен null и содержит две подзадачи
        assertNotNull(subtasksFromManager, "Подзадачи не должны быть null");
        assertEquals(2, subtasksFromManager.size(), "Неверное количество подзадач");
    }

    @Test
    void shouldGetSubtaskById() throws IOException, InterruptedException {
        taskManager.addNewEpic(new Epic("Epic 1", "DescriptionForEpic_1"));

        Subtask subtask1 = new Subtask("Subtask 1", "DescriptionForSubtask_1", TaskStatus.DONE, 1,
                Duration.ZERO, LocalDateTime.now());
        subtask1.setStartTime(LocalDateTime.of(2025, 2, 11, 12, 0));
        subtask1.setDuration(Duration.ofHours(3));

        Subtask subtask2 = new Subtask("Subtask 2", "DescriptionForSubtask_2", TaskStatus.IN_PROGRESS, 1,
                Duration.ZERO, LocalDateTime.now());
        subtask2.setStartTime(LocalDateTime.of(2025, 2, 11, 16, 0));
        subtask2.setDuration(Duration.ofHours(2));

        String subtaskJson1 = gson.toJson(subtask1);
        String subtaskJson2 = gson.toJson(subtask2);

        URI baseUrl = URI.create("http://localhost:8080/subtasks");
        URI subtaskUrl = URI.create("http://localhost:8080/subtasks/2");

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest requestAddSubtask1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson1))
                .uri(baseUrl)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        HttpRequest requestAddSubtask2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson2))
                .uri(baseUrl)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        client.send(requestAddSubtask1, HttpResponse.BodyHandlers.ofString());
        client.send(requestAddSubtask2, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestGetSubtask = HttpRequest.newBuilder()
                .GET()
                .uri(subtaskUrl)
                .header("Accept", "application/json")
                .build();

        //  GET запрос и ответ
        HttpResponse<String> responseGet = client.send(requestGetSubtask, HttpResponse.BodyHandlers.ofString());

        // проверка ответа
        assertEquals(200, responseGet.statusCode(), "Статус ответа должен быть 200");

        Subtask responseSubTask = gson.fromJson(responseGet.body(), Subtask.class);

        // полученная подзадача соответствует ожидаемой
        assertEquals(taskManager.getSubtask(2).getName(), responseSubTask.getName(), "Заголовок подзадачи не совпадает");
    }

    @Test
    void shouldUpdateSubtask() throws IOException, InterruptedException {
        taskManager.addNewEpic(new Epic("Epic 1", "DescriptionForEpic_1"));

        Subtask subtask11 = new Subtask("Subtask 1", "DescriptionForSubtask_1", TaskStatus.DONE, 1,
                Duration.ZERO,LocalDateTime.now());
        subtask11.setStartTime(LocalDateTime.of(2025, 2, 11, 12, 0));
        subtask11.setDuration(Duration.ofHours(3));

        Subtask subtask12 = new Subtask("Subtask 2", "DescriptionForSubtask_2", TaskStatus.IN_PROGRESS, 1,
                Duration.ZERO,LocalDateTime.now());
        subtask12.setStartTime(LocalDateTime.of(2025, 2, 11, 16, 0));
        subtask12.setDuration(Duration.ofHours(2));

        String subtaskJson11 = gson.toJson(subtask11);
        String subtaskJson12 = gson.toJson(subtask12);

        URI url = URI.create("http://localhost:8080/subtasks");
        URI url2 = URI.create("http://localhost:8080/subtasks/2");

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson11))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson12))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(url2)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(request1, handler);
        client.send(request2, handler);
        HttpResponse<String> responseGet = client.send(requestGet, handler);

        Task responseSubtask = gson.fromJson(responseGet.body(), Subtask.class);
        responseSubtask.setStatus(TaskStatus.DONE);

        String responseSubtaskToJson = gson.toJson(responseSubtask);

        HttpRequest requestUpdateTask = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(responseSubtaskToJson))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> responseUpdateSubtask = client.send(requestUpdateTask, handler);

        assertEquals(201, responseUpdateSubtask.statusCode());

        HttpResponse<String> responseAfterUpdate = client.send(requestGet, handler);

        assertEquals(200, responseAfterUpdate.statusCode());

        Subtask responseSubtaskAfterUpdate = gson.fromJson(responseAfterUpdate.body(), Subtask.class);

        assertEquals(200, responseAfterUpdate.statusCode());
        assertEquals(responseSubtask.getStatus(), responseSubtaskAfterUpdate.getStatus());
    }

    @Test
    void shouldDeleteSubtaskById() throws IOException, InterruptedException {
        taskManager.addNewEpic(new Epic("Epic 1", "DescriptionForEpic_1"));

        Subtask subtask11 = new Subtask("Subtask 1", "DescriptionForSubtask_1", TaskStatus.DONE, 1,
                Duration.ZERO,LocalDateTime.now());
        subtask11.setStartTime(LocalDateTime.of(2025, 2, 11, 12, 0));
        subtask11.setDuration(Duration.ofHours(3));

        Subtask subtask12 = new Subtask("Subtask 2", "DescriptionForSubtask_2", TaskStatus.IN_PROGRESS, 1,
                Duration.ZERO,LocalDateTime.now());
        subtask12.setStartTime(LocalDateTime.of(2025, 2, 11, 16, 0));
        subtask12.setDuration(Duration.ofHours(2));

        String subtaskJson11 = gson.toJson(subtask11);
        String subtaskJson12 = gson.toJson(subtask12);

        URI url = URI.create("http://localhost:8080/subtasks");
        URI url2 = URI.create("http://localhost:8080/subtasks/2");

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson11))
                .uri(url)
                .header("Accept", "application/json")
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson12))
                .uri(url)
                .header("Accept", "application/json")
                .build();

        client.send(request1, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());

        // удаляем подзадачу с ID 2
        HttpRequest requestDelete = HttpRequest.newBuilder()
                .DELETE()
                .uri(url2)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseDelete.statusCode());
        assertNull(taskManager.getTask(2));

        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(url2)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseGet.statusCode());
    }
}
