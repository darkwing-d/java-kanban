package managers;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testAddAndGetTask() {
        super.testAddAndGetTask();
    }

    @Test
    public void testAddAndGetEpic() {
        super.testAddAndGetEpic();
    }

    @Test
    public void testAddAndGetSubtask() {
        super.testAddAndGetSubtask();
    }

    @Test
    public void testGetNonExistentTask() {
        super.testGetNonExistentTask();
    }

    @Test
    public void testGetNonExistentEpic() {
        super.testGetNonExistentEpic();
    }

    @Test
    public void testGetNonExistentSubtask() {
        super.testGetNonExistentSubtask();
    }

    @Test
    public void testTaskIdCollision() {
        // Добавляем задачу с заданным ID
        Task taskWithId = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ZERO,
                LocalDateTime.now());
        taskManager.addNewTask(taskWithId);

        assertNotNull(taskWithId);
        assertEquals(1, taskWithId.getId());

        // Добавляем задачу с генерированным ID
        Task taskWithGeneratedId = new Task("Task 2", "Description 2", TaskStatus.NEW, Duration.ZERO,
                LocalDateTime.now().plusMinutes(1));
        taskManager.addNewTask(taskWithGeneratedId);
        assertNotNull(taskWithGeneratedId);
        assertEquals(2, taskWithGeneratedId.getId()); // равен значению, обозначающему сгенерированный ID

        // Проверяем, что задачи с заданными ID не конфликтуют
        Assertions.assertTrue(taskManager.taskExists(1)); // Задача с ID 1 должна существовать
        Assertions.assertFalse(taskManager.taskExists(-1)); // Задача с генерированным ID не должна конфликтовать с другими
    }

    @Test
    public void testTaskImmutabilityUponAdding() {
        super.testTaskImmutabilityUponAdding();
    }
}
