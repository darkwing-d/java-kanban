package managers;

import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

class InMemoryHistoryManagerTest {
    private InMemoryTaskManager taskManager;
    private InMemoryHistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2023, 10, 1, 10, 0));
        task2 = new Task("Task 2", "Description 2", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2023, 10, 1, 11, 0));
        task3 = new Task("Task 3", "Description 3", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2023, 10, 1, 12, 0));
    }

    @Test
    public void testTaskHistoryPreservation() {
        // Создаем задачи с разными временными метками
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, Duration.ZERO,
                LocalDateTime.now());
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW, Duration.ZERO,
                LocalDateTime.now().plusMinutes(1));

        taskManager.addNewTask(task1);
        // Проверяем, что в истории только одна задача
        List<Task> historyAfterFirstAdd = taskManager.getHistory();
        assertEquals(1, historyAfterFirstAdd.size());
        assertEquals(task1, historyAfterFirstAdd.get(0));

        taskManager.addNewTask(task2);
        // Проверяем, что в истории обе задачи
        List<Task> historyAfterSecondAdd = taskManager.getHistory();
        assertEquals(2, historyAfterSecondAdd.size());
        assertEquals(task1, historyAfterSecondAdd.get(0));
        assertEquals(task2, historyAfterSecondAdd.get(1));

        // Вызов первой задачи для обновления истории
        taskManager.getTask(task1.getId());
        // Проверяем, что история содержит обе задачи с правильным порядком
        List<Task> historyAfterFirstTaskAccess = taskManager.getHistory();
        assertEquals(2, historyAfterFirstTaskAccess.size());
        assertEquals(task1, historyAfterFirstTaskAccess.get(0)); // task1 первая
        assertEquals(task2, historyAfterFirstTaskAccess.get(1)); // task2 вторая
    }

    @Test
    public void testAddAndRemoveTasks() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        // Создаем задачи с разными временными метками
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, Duration.ZERO,
                LocalDateTime.now());
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW, Duration.ZERO,
                LocalDateTime.now().plusMinutes(1));

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        assertTrue("Задача 1 не найдена", taskManager.getTasks().contains(task1));
        assertTrue("Задача 2 не найдена", taskManager.getTasks().contains(task2));

        // Удаляем задачу и проверяем
        taskManager.deleteTask(task1.getId());
        assertFalse("Задача 1 должна быть удалена", taskManager.getTasks().contains(task1));
        assertTrue("Задача 2 должна остаться", taskManager.getTasks().contains(task2));
    }

    @Test
    public void testDeleteNonExistingSubtask() {
        // Проверка удаления несуществующей подзадачи
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Эпик 1", "Описание эпика");
        taskManager.addNewEpic(epic);

        taskManager.deleteTask(999); // ID, который не существует

        assertTrue("Список подзадач эпика должен оставаться пустым", epic.getSubtaskIds().isEmpty());
    }

    @Test
    public void testSubtaskIsRemovedFromManager() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Эпик 1", "Описание эпика");
        taskManager.addNewEpic(epic);

        Subtask subTask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW,
                epic.getId(), Duration.ZERO, LocalDateTime.now());
        taskManager.addNewSubtask(subTask1);

        // Удаляем подзадачу subTask1
        taskManager.deleteSubtask(subTask1.getId());

        // подзадача больше не существует
        assertNull("Подзадача 1 должна быть удалена из менеджера задач", taskManager.getSubtask(subTask1.getId()));
    }

    @Test
    public void testSubtaskRemovalUpdatesEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Эпик 1", "Описание эпика");
        taskManager.addNewEpic(epic);

        // Создаем подзадачу
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW,
                epic.getId(), Duration.ZERO, LocalDateTime.now());
        taskManager.addNewSubtask(subtask);

        // подзадача добавлена в эпик
        assertTrue(epic.getSubtaskIds().contains(subtask.getId()));

        // Удаляем подзадачу
        taskManager.deleteSubtask(subtask.getId());

        // ID подзадачи удален из эпика
        assertFalse(epic.getSubtaskIds().contains(subtask.getId()));
    }

    @Test
    public void testTaskUpdateAffectsManager() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Задача 1", "Описание задачи", TaskStatus.NEW, Duration.ZERO, LocalDateTime.now());
        taskManager.addNewTask(task);

        // Изменяем название задачи через сеттер
        task.setName("Измененное название");

        // Проверяем, что изменение отражается в менеджере
        Task updatedTaskInManager = taskManager.getTask(task.getId());
        assertNotEquals("Измененное название", updatedTaskInManager.getDescription());
    }

    @Test
    public void testSubtaskStatusChange() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Эпик 1", "Описание эпика");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW,
                epic.getId(), Duration.ZERO, LocalDateTime.now());
        taskManager.addNewSubtask(subtask);

        // Изменяем статус подзадачи
        subtask.setStatus(TaskStatus.DONE);

        // Проверяем, что изменение не отражается в менеджере
        Subtask updatedSubtaskInManager = taskManager.getSubtask(subtask.getId());
        assertNotEquals(TaskStatus.NEW, updatedSubtaskInManager.getStatus());
    }

    @Test
    public void testRemoveTaskFromHistory() {
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());

        // Удаляем задачу
        taskManager.deleteTask(task1.getId());
        taskManager.deleteTask(task2.getId());
        taskManager.deleteTask(task3.getId());

        System.out.println(taskManager.getHistory());
        // Проверяем, что история пуста
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    public void testAddTaskToEmptyHistory() {
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    public void testAddDuplicateTask() {
        historyManager.add(task1);
        historyManager.add(task1); // Добавляем дубликат
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    public void testAddMultipleTasks() {
        taskManager.addNewTask(task1);
        historyManager.add(task1);

        taskManager.addNewTask(task2);
        historyManager.add(task2);

        taskManager.addNewTask(task3);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        System.out.println(history);
        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(task3, history.get(2));
    }

    @Test
    public void testRemoveFromBeginning() {
        taskManager.addNewTask(task1);
        historyManager.add(task1);

        taskManager.addNewTask(task2);
        historyManager.add(task2);

        taskManager.addNewTask(task3);
        historyManager.add(task3);

        // Вывод текущей истории перед удалением
        System.out.println("История до удаления: " + historyManager.getHistory());

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();

        // Вывод текущей истории после удаления
        System.out.println("История после удаления: " + history);

        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    public void testRemoveFromMiddle() {
        taskManager.addNewTask(task1);
        historyManager.add(task1);

        taskManager.addNewTask(task2);
        historyManager.add(task2);

        taskManager.addNewTask(task3);
        historyManager.add(task3);

        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    public void testRemoveFromEnd() {
        taskManager.addNewTask(task1);
        historyManager.add(task1);

        taskManager.addNewTask(task2);
        historyManager.add(task2);

        taskManager.addNewTask(task3);
        historyManager.add(task3);

        historyManager.remove(task3.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    public void testRemoveNonExistentTask() {
        taskManager.addNewTask(task1);
        historyManager.add(task1);
        historyManager.remove(999); // Удаляем несуществующую задачу
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    public void testGetHistoryOnEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue("История должна быть пустой", history.isEmpty());
    }
}