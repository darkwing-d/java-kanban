package managers;

import manager.FileBackedTaskManager;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void testSaveAndLoadEmptyFile() throws IOException {
        File tempFile = File.createTempFile("empty_tasks", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(manager.getTasks().isEmpty());
        assertTrue(manager.getEpics().isEmpty());
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    void testSaveMultipleTasks() throws IOException {
        File tempFile = File.createTempFile("multiple_tasks", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile.getAbsolutePath());

        // Создаем задачи с уникальными временными метками
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task("Task 1", "Description for task 1", TaskStatus.NEW,
                Duration.ofHours(1), now); // Начало в now
        Task task2 = new Task("Task 2", "Description for task 2", TaskStatus.IN_PROGRESS,
                Duration.ofHours(1), now.plusHours(3)); // Начало через 3 часа
        Epic epic = new Epic("Epic 1", "Description for epic 1");

        manager.addNewTask(task1);
        manager.addNewTask(task2);

        // Добавление эпика
        Integer epicId = manager.addNewEpic(epic);
        assertNotNull(epicId, "ID не должен быть равен нулю после добавления.");

        // Добавление подзадачи с уникальным временем начала
        Subtask subtask = new Subtask("Subtask 1", "Description for subtask 1", TaskStatus.NEW, epicId,
                Duration.ofHours(1), now.plusHours(2)); // Начало через 2 часа

        // Подзадача
        Integer subtaskId = manager.addNewSubtask(subtask);
        assertNotNull(subtaskId, "ID не должен быть равен нулю после добавления.");

        // Сохранение
        manager.saveTasks();

        // Загрузка из файла в менеджер
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверка количества задач
        assertEquals(2, loadedManager.getTasks().size());
        assertEquals(1, loadedManager.getEpics().size());
        assertEquals(1, loadedManager.getSubtasks().size());

        // Проверка загруженных задач
        assertEquals(task1.getName(), loadedManager.getTasks().get(0).getName());
        assertEquals(task2.getName(), loadedManager.getTasks().get(1).getName());
        assertEquals(epic.getName(), loadedManager.getEpics().get(0).getName());
        assertEquals(subtask.getName(), loadedManager.getSubtasks().get(0).getName());

        // Проверка статуса, описания, продолжительности и времени начала
        assertEquals(task1.getStatus(), loadedManager.getTasks().get(0).getStatus());
        assertEquals(task2.getStatus(), loadedManager.getTasks().get(1).getStatus());
        assertEquals(epic.getDescription(), loadedManager.getEpics().get(0).getDescription());
        assertEquals(subtask.getDescription(), loadedManager.getSubtasks().get(0).getDescription());

        // Проверка продолжительности и времени начала
<<<<<<< HEAD
        assertEquals(task1.getEpicDuration(), loadedManager.getTasks().get(0).getEpicDuration());
        assertEquals(task2.getEpicDuration(), loadedManager.getTasks().get(1).getEpicDuration());
=======
        assertEquals(task1.getDuration(), loadedManager.getTasks().get(0).getDuration());
        assertEquals(task2.getDuration(), loadedManager.getTasks().get(1).getDuration());
>>>>>>> ed885c911858f8f895bf6947c034756162fc4052
        assertEquals(task1.getStartTime(), loadedManager.getTasks().get(0).getStartTime());
        assertEquals(task2.getStartTime(), loadedManager.getTasks().get(1).getStartTime());
        assertEquals(subtask.getStartTime(), loadedManager.getSubtasks().get(0).getStartTime()); // Проверяем время начала подзадачи
    }

    @Test
    void testLoadMultipleTasks() throws IOException {
        File tempFile = File.createTempFile("load_tasks", ".csv");
        tempFile.deleteOnExit();

        String testData = String.join("\n",
                "1,TASK,Task 1,NEW,Description for task 1,PT10M,2023-10-10T10:00:00", // Начало в 10:00
                "2,TASK,Task 2,IN_PROGRESS,Description for task 2,PT15M,2023-10-10T10:20:00", // Начало в 10:20
                "3,EPIC,Epic 1,NEW,Description for epic 1,PT0M,2023-10-10T10:30:00", // Начало в 10:30
                "4,SUBTASK,Subtask 1,NEW,Description for subtask 1,PT5M,2023-10-10T10:35:00,3" // Подзадача для эпика, начало в 10:35
        );
        Files.write(tempFile.toPath(), testData.getBytes(StandardCharsets.UTF_8));

        // Загружаем задачи из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем количество задач и эпиков
        assertEquals(2, loadedManager.getTasks().size(), "Количество задач должно быть 2");
        assertEquals(1, loadedManager.getEpics().size(), "Количество эпиков должно быть 1");
        assertEquals(1, loadedManager.getSubtasks().size(), "Количество подзадач должно быть 1");

        // Проверяем названия задач
        assertEquals("Task 1", loadedManager.getTasks().get(0).getName(), "Имя первой задачи должно совпадать");
        assertEquals("Task 2", loadedManager.getTasks().get(1).getName(), "Имя второй задачи должно совпадать");
        assertEquals("Epic 1", loadedManager.getEpics().get(0).getName(), "Имя эпика должно совпадать");

        // Проверка подзадачи
        assertEquals("Subtask 1", loadedManager.getSubtasks().get(0).getName(), "Имя подзадачи должно совпадать");
    }
}
