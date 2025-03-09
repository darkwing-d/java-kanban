package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Task;

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

public class HttpEpicHandlerTest {

    private final InMemoryTaskManager taskManager = new InMemoryTaskManager();
    private final Gson gson = Managers.createGson(); // Gson с адаптерами
    HttpTaskServer taskServer;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "DescriptionForEpic_1");
        epic.setStartTime(LocalDateTime.now());
        epic.setDuration(Duration.ofHours(2));

        String epicJson = gson.toJson(epic);
        System.out.println("Сериализованный JSON: " + epicJson);

        URI uri = URI.create("http://localhost:8080/epics");

        // HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .uri(uri)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        //  запрос и получение ответа
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //  статус ответа
        assertEquals(201, response.statusCode(), "Статус ответа должен быть 201");

        List<Epic> epicsFromManager = taskManager.getEpics();

        assertNotNull(epicsFromManager, "Список эпиков не должен быть null");
        assertEquals(1, epicsFromManager.size(), "Должен быть один эпик в менеджере");
        assertEquals("Epic 1", epicsFromManager.get(0).getName(), "Имя добавленного эпика некорректное");
    }

    @Test
    void shouldGetEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик №1", "Съездить в отпуск");
        Epic epic2 = new Epic("Эпик №2", "Съездить в отпуск 2 раза");

        String epicJson1 = gson.toJson(epic1);
        String epicJson2 = gson.toJson(epic2);

        URI url = URI.create("http://localhost:8080/epics");
        URI epicUrl = URI.create("http://localhost:8080/epics/2");

        HttpClient client = HttpClient.newHttpClient();

        // запросы на создание эпиков
        HttpRequest request1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicJson1))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicJson2))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        // запрос на получение эпика по ID
        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(epicUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        // ответ
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        client.send(request1, handler);
        client.send(request2, handler);

        HttpResponse<String> responseGet = client.send(requestGet, handler);

        System.out.println("Ответ сервера: " + responseGet.body());
        System.out.println("Статус ответа: " + responseGet.statusCode());

        assertEquals(200, responseGet.statusCode());

        // ответ в Task
        Task responseEpic = gson.fromJson(responseGet.body(), Task.class);

        assertEquals(epic2.getName(), responseEpic.getName());
    }

    @Test
    void shouldDeleteEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "DescriptionForEpic_1");
        Epic epic2 = new Epic("Epic 2", "DescriptionForEpic_2");

        String epicJson1 = gson.toJson(epic1);
        String epicJson2 = gson.toJson(epic2);
        URI url = URI.create("http://localhost:8080/epics");
        URI epicUrl2 = URI.create("http://localhost:8080/epics/2");

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest requestCreateEpic1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicJson1))
                .uri(url)
                .header("Accept", "application/json")
                .build();

        HttpRequest requestCreateEpic2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicJson2))
                .uri(url)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> responseCreateEpic1 = client.send(requestCreateEpic1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseCreateEpic2 = client.send(requestCreateEpic2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseCreateEpic1.statusCode());
        assertEquals(201, responseCreateEpic2.statusCode());

        // запрос на удаление эпика по ID
        HttpRequest requestDeleteEpic = HttpRequest.newBuilder()
                .DELETE()
                .uri(epicUrl2)
                .header("Accept", "application/json")
                .build();

        // запрос на удаление эпика
        HttpResponse<String> responseDelete = client.send(requestDeleteEpic, HttpResponse.BodyHandlers.ofString());

        assertTrue(responseDelete.statusCode() == 200 || responseDelete.statusCode() == 204);

        assertNull(taskManager.getEpic(2));

        // запрос на получение удаленного эпика
        HttpRequest requestGetEpic = HttpRequest.newBuilder()
                .GET()
                .uri(epicUrl2)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> responseGet = client.send(requestGetEpic, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 404 (эпик не найден)
        assertEquals(404, responseGet.statusCode());
    }

    @Test
    void shouldGetEpics() throws IOException, InterruptedException {
        Type epicType = new TypeToken<List<Epic>>() {
        }.getType();

        Epic epic1 = new Epic("Epic 1", "DescriptionForEpic_1");
        Epic epic2 = new Epic("Epic 2", "DescriptionForEpic_2");

        String epicJson1 = gson.toJson(epic1);
        String epicJson2 = gson.toJson(epic2);

        URI epicsUrl = URI.create("http://localhost:8080/epics");

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest createEpicRequest1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicJson1))
                .uri(epicsUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpRequest createEpicRequest2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicJson2))
                .uri(epicsUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse.BodyHandler<String> responseHandler = HttpResponse.BodyHandlers.ofString();
        client.send(createEpicRequest1, responseHandler);
        client.send(createEpicRequest2, responseHandler);

        // запрос на получение всех эпиков
        HttpRequest getEpicsRequest = HttpRequest.newBuilder()
                .GET()
                .uri(epicsUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> getEpicsResponse = client.send(getEpicsRequest, responseHandler);

        assertEquals(200, getEpicsResponse.statusCode(), "Expected status 200 for GET request to retrieve epics");

        List<Task> epicsFromManager = gson.fromJson(getEpicsResponse.body(), epicType);

        assertNotNull(epicsFromManager, "Expected non-null list of epics");
        assertEquals(2, epicsFromManager.size(), "Expected 2 epics to be present in the list");
    }
}