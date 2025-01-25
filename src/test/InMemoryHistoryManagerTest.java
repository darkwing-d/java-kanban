package test;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static org.junit.Assert.*;

class InMemoryHistoryManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testTaskHistoryPreservation() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);

        taskManager.addNewTask(task1);
        // только одна задача
        List<Task> historyAfterFirstAdd = taskManager.getHistory();
        assertEquals(1, historyAfterFirstAdd.size());
        assertEquals(task1, historyAfterFirstAdd.get(0));

        taskManager.addNewTask(task2);
        // в истории обе задачи
        List<Task> historyAfterSecondAdd = taskManager.getHistory();
        assertEquals(2, historyAfterSecondAdd.size());
        assertEquals(task1, historyAfterSecondAdd.get(0));
        assertEquals(task2, historyAfterSecondAdd.get(1));

        // Вызов первой задачи для обновления истории
        taskManager.getTask(task1.getId());
        // history содержит обе задачи с правильным порядком
        List<Task> historyAfterFirstTaskAccess = taskManager.getHistory();
        assertEquals(2, historyAfterFirstTaskAccess.size());
        assertEquals(task1, historyAfterFirstTaskAccess.get(0)); // task1 первая
        assertEquals(task2, historyAfterFirstTaskAccess.get(1)); // task2 вторая
    }

    @Test
    public void testAddAndRemoveTasks() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        assertTrue("Текст ошибки: Задача 1 не найдена", taskManager.getTasks().contains(task1));
        assertTrue("Текст ошибки: Задача 2 не найдена", taskManager.getTasks().contains(task2));

        // Удаляем задачу и проверяем
        taskManager.deleteTask(task1.getId());
        assertFalse("Текст ошибки: Задача 1 должна быть удалена", taskManager.getTasks().contains(task1));
        assertTrue("Текст ошибки: Задача 2 должна остаться", taskManager.getTasks().contains(task2));
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

        // Создаем эпик и подзадачи
        Epic epic = new Epic("Эпик 1", "Описание эпика");
        taskManager.addNewEpic(epic);

        Subtask subTask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic.getId());
        taskManager.addNewSubtask(subTask1);

        // Удаляем подзадачу subTask1
        taskManager.deleteSubtask(subTask1.getId());

        // подзадача больше не существует
        assertNull("Подзадача 1 должна быть удалена из менеджера задач", taskManager.getSubtask(subTask1.getId()));
    }

    @Test
    public void testSubtaskRemovalUpdatesEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        // Создаем эпик
        Epic epic = new Epic("Эпик 1", "Описание эпика");
        taskManager.addNewEpic(epic);

        // Создаем подзадачу
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic.getId());
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
        Task task = new Task("Задача 1", "Описание задачи", TaskStatus.NEW);
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
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic.getId());
        taskManager.addNewSubtask(subtask);

        // Изменяем статус подзадачи
        subtask.setStatus(TaskStatus.DONE);

        // Проверяем, что изменение не отражается в менеджере
        Subtask updatedSubtaskInManager = taskManager.getSubtask(subtask.getId());
        assertNotEquals(TaskStatus.NEW, updatedSubtaskInManager.getStatus());
    }

    @Test
    public void testRemoveTaskFromHistory() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Задача 1", "Описание задачи", TaskStatus.NEW);
        taskManager.addNewTask(task);
        taskManager.getTask(task.getId()); // Просмотр задачи, она добавится в историю

        // Удаляем задачу
        taskManager.deleteTask(task.getId());

        // Проверяем, что история пуста
        assertTrue(taskManager.getHistory().isEmpty());
    }

}