package test;

import manager.FileBackedTaskManager;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.IOException;

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

        Task task1 = new Task("Task 1", "Description for task 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description for task 2", TaskStatus.IN_PROGRESS);
        Epic epic = new Epic("Epic 1", "Description for epic 1");

        manager.addNewTask(task1);
        manager.addNewTask(task2);

        // добавление эпика
        Integer epicId = manager.addNewEpic(epic);
        assertNotNull(epicId, "ID не должен быть равен нулю после добавления.");

        // добавление подзадачи с нужным id существующего эпика
        Subtask subtask = new Subtask("Subtask 1", "Description for subtask 1", TaskStatus.NEW, epicId);

        // подзадача
        Integer subtaskId = manager.addNewSubtask(subtask);
        assertNotNull(subtaskId, "ID не должен быть равен нулю после добавления.");

        // сохранение
        manager.save();

        // загрузка из файла в менеджер
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // проверка количества задач
        assertEquals(2, loadedManager.getTasks().size());
        assertEquals(1, loadedManager.getEpics().size());
        assertEquals(1, loadedManager.getSubtasks().size());

        // проверка загруженных задач
        assertEquals(task1.getName(), loadedManager.getTasks().get(0).getName());
        assertEquals(task2.getName(), loadedManager.getTasks().get(1).getName());
        assertEquals(epic.getName(), loadedManager.getEpics().get(0).getName());
        assertEquals(subtask.getName(), loadedManager.getSubtasks().get(0).getName());

        // проверка статуса и описания
        assertEquals(task1.getStatus(), loadedManager.getTasks().get(0).getStatus());
        assertEquals(task2.getStatus(), loadedManager.getTasks().get(1).getStatus());
        assertEquals(epic.getDescription(), loadedManager.getEpics().get(0).getDescription());
        assertEquals(subtask.getDescription(), loadedManager.getSubtasks().get(0).getDescription());
    }

    @Test
    void testLoadMultipleTasks() throws IOException {
        File tempFile = File.createTempFile("load_tasks", ".csv");
        tempFile.deleteOnExit();

        // Пишем тестовые данные в файл
        String testData =
                "1,TASK,Task 1,NEW,Description for task 1,,\n" +
                        "2,TASK,Task 2,IN_PROGRESS,Description for task 2,,\n" +
                        "3,EPIC,Epic 1,NEW,Description for epic 1,,\n";
        java.nio.file.Files.write(tempFile.toPath(), testData.getBytes());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getTasks().size());
        assertEquals(1, loadedManager.getEpics().size());
        assertEquals("Task 1", loadedManager.getTasks().get(0).getName());
        assertEquals("Task 2", loadedManager.getTasks().get(1).getName());
        assertEquals("Epic 1", loadedManager.getEpics().get(0).getName());
    }
}
