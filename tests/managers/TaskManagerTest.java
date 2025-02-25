package managers;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    private Task originalTask;

    @Test
    public void testAddAndGetTask() {
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, Duration.ZERO, LocalDateTime.now());
        taskManager.addNewTask(task);

        Task retrievedTask = taskManager.getTask(task.getId());

        assertNotNull(retrievedTask, "Задача должна быть найдена");
        assertEquals(task.getId(), retrievedTask.getId(), "Идентификаторы должны совпадать");
        assertEquals(task.getName(), retrievedTask.getName(), "Имена задач должны совпадать");
    }

    @Test
    public void testAddAndGetEpic() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addNewEpic(epic);

        Epic retrievedEpic = taskManager.getEpic(epic.getId());

        assertNotNull(retrievedEpic, "Эпик должен быть найден");
        assertEquals(epic.getId(), retrievedEpic.getId(), "Идентификаторы должны совпадать");
        assertEquals(epic.getName(), retrievedEpic.getName(), "Имена эпиков должны совпадать");
    }

    @Test
    public void testAddAndGetSubtask() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW,
                epic.getId(), Duration.ZERO, LocalDateTime.now());
        taskManager.addNewSubtask(subtask);

        Subtask retrievedSubtask = taskManager.getSubtask(subtask.getId());

        assertNotNull(retrievedSubtask, "Подзадача должна быть найдена");
        assertEquals(subtask.getId(), retrievedSubtask.getId(), "Идентификаторы должны совпадать");
        assertEquals(subtask.getName(), retrievedSubtask.getName(), "Имена подзадач должны совпадать");
    }

    @Test
    public void testGetNonExistentTask() {
        Task retrievedTask = taskManager.getTask(999); // Не существующий ID
        assertNull(retrievedTask, "Не существующая задача должна возвращать null");
    }

    @Test
    public void testGetNonExistentEpic() {
        Epic retrievedEpic = taskManager.getEpic(999); // Не существующий ID
        assertNull(retrievedEpic, "Не существующий эпик должен возвращать null");
    }

    @Test
    public void testGetNonExistentSubtask() {
        Subtask retrievedSubtask = taskManager.getSubtask(999); // Не существующий ID
        assertNull(retrievedSubtask, "Не существующая подзадача должна возвращать null");
    }

    @Test
    public void testTaskImmutabilityUponAdding() {
        originalTask = new Task("Original Task", "Description of Original Task", TaskStatus.NEW,
                Duration.ZERO, LocalDateTime.now());

        // Сохраняем оригинальные значения
        int originalId = originalTask.getId();
        String originalName = originalTask.getName();
        String originalDescription = originalTask.getDescription();
        TaskStatus originalStatus = originalTask.getStatus();

        // Добавляем задачу в менеджер
        Task task = new Task(originalTask.getName(), originalTask.getDescription(), originalTask.getStatus(),
                Duration.ZERO, LocalDateTime.now());
        taskManager.addNewTask(task);

        // Проверяем, что все поля задачи остались неизменными
        assertEquals(originalId, originalTask.getId());
        assertEquals(originalName, originalTask.getName());
        assertEquals(originalDescription, originalTask.getDescription());
        assertEquals(originalStatus, originalTask.getStatus());
    }

    @Test
    public void testEpicStatusUpdatesWithSubtasks() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("epic", "epic_description");
        manager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", TaskStatus.NEW, epic.getId(),
                Duration.ZERO, LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", TaskStatus.DONE, epic.getId(),
                Duration.ZERO, LocalDateTime.now().plusMinutes(1));

        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicStatus(epic.getId()), "Статус эпика IN_PROGRESS");
    }

    @Test
    public void testEmptyEpicStatus() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        // Создаем эпик без подзадач
        Epic epic = new Epic("epic", "epic_description");
        manager.addNewEpic(epic);

        // Проверяем, что статус эпика по умолчанию NEW
        assertEquals(TaskStatus.NEW, manager.getEpicStatus(epic.getId()), "Статус эпика - NEW");
    }

    @Test
    public void testSubtaskLinksToEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("epic", "epic_description");
        manager.addNewEpic(epic);

        // Создаем подзадачу, связав её с эпиком по ID
        Subtask subtask = new Subtask("Subtask 1", "Description", TaskStatus.NEW, epic.getId(),
                Duration.ZERO, LocalDateTime.now());
        manager.addNewSubtask(subtask);

        // Проверяем, что подзадача действительно связана с эпиком
        Epic fetchedEpic = manager.getEpic(epic.getId());
        assertTrue(fetchedEpic.getSubtaskIds().contains(subtask.getId()), "Epic should contain the subtask ID");
    }

    @Test
    public void testExceptionOnFileRead() {
        assertThrows(IOException.class, () -> {
            Files.readAllLines(Path.of("non_existent_file.txt"));
        }, "Чтение из несуществующего файла должно приводить к исключению");
    }

    @Test
    public void testExceptionOnFileWrite() {
        assertThrows(IOException.class, () -> {
            Files.writeString(Path.of("invalid_path/invalid_file.txt"), "Test content");
        }, "Запись в недоступный путь должна приводить к исключению");
    }

    @Test
    public void testSuccessfulFileOperation() {
        assertDoesNotThrow(() -> {
            Path tempFile = Files.createTempFile("testFile", ".txt");
            Files.writeString(tempFile, "Test content");
            String content = Files.readString(tempFile);
            assertEquals("Test content", content);
            Files.delete(tempFile); // Удаляем временный файл
        }, "Операция с файлом должна выполняться без исключений");
    }
}