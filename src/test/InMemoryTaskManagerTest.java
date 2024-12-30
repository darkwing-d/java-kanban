package test;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private Task originalTask;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testAddAndGetTask() {
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
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

        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic.getId());
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
    public void testTaskIdCollision() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        // Добавляем задачу с заданным ID
        Task taskWithId = new Task("Task 1", "Description 2", TaskStatus.NEW);
        taskManager.addNewTask(taskWithId);

        assertNotNull(taskWithId);
        assertEquals(1, taskWithId.getId());

        // Добавляем задачу с генерированным ID
        Task taskWithGeneratedId = new Task("Task 2", "Description 2", TaskStatus.NEW);
        taskManager.addNewTask(taskWithGeneratedId);
        assertNotNull(taskWithGeneratedId);
        assertEquals(2, taskWithGeneratedId.getId()); // Убедитесь, что он равен 2 или другому значению, обозначающему сгенерированный ID

        // Проверяем, что задачи с заданными ID не конфликтуют
        assertTrue(taskManager.taskExists(1)); // Задача с ID 1 должна существовать
        assertFalse(taskManager.taskExists(-1)); // Задача с генерированным ID не должна конфликтовать с другими
    }

    @Test
    public void testTaskImmutabilityUponAdding() {
        taskManager = new InMemoryTaskManager();
        originalTask = new Task("Original Task", "Description of Original Task", TaskStatus.NEW);

        // Сохраняем оригинальные значения
        int originalId = originalTask.getId();
        String originalName = originalTask.getName();
        String originalDescription = originalTask.getDescription();
        TaskStatus originalStatus = originalTask.getStatus();

        // Добавляем задачу в менеджер
        Task task = new Task(originalTask.getName(), originalTask.getDescription(), originalTask.getStatus());
        taskManager.addNewTask(task);

        // Проверяем, что все поля задачи остались неизменными
        assertEquals(originalId, originalTask.getId());
        assertEquals(originalName, originalTask.getName());
        assertEquals(originalDescription, originalTask.getDescription());
        assertEquals(originalStatus, originalTask.getStatus());
    }
}