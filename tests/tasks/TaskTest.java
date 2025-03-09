package tasks;

import exceptions.TimeException;
import manager.*;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TaskTest {

    @Test
    void testTasksEquality() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ZERO, LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW, Duration.ZERO, LocalDateTime.now());

        assertNotEquals(task1, task2);

        int taskId = task1.getId();
        Task task3 = new Task(task1.getName(), task1.getDescription(), task1.getStatus(), Duration.ZERO, LocalDateTime.now());

        task3.setId(taskId);

        assertEquals(task1, task3); // Задачи с одинаковыми id ==
    }

    @Test
    void testSubtaskCreation() {
        Subtask subtask = new Subtask("Subtask 1", "Description", TaskStatus.NEW, 1,
                Duration.ZERO, LocalDateTime.now());
        assertEquals("Subtask 1", subtask.getName());
        assertEquals("Description", subtask.getDescription());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(1, subtask.getEpicId());
    }

    @Test
    void testEqualsAndHashCode() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description", TaskStatus.NEW, 1,
                Duration.ZERO, LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 1", "Description", TaskStatus.NEW, 1,
                Duration.ZERO, LocalDateTime.now());
        Subtask subtask3 = new Subtask("Subtask 2", "Other Description", TaskStatus.NEW, 2,
                Duration.ZERO, LocalDateTime.now());

        assertEquals(subtask1, subtask2); // Они равны, потому что идентификаторы эпиков идентичны
        assertNotEquals(subtask1, subtask3); // Разные epicId
        assertEquals(subtask1.hashCode(), subtask2.hashCode()); // Хэш-коды равны
        assertNotEquals(subtask1.hashCode(), subtask3.hashCode()); // Хэш-коды разные
    }

    @Test
    public void testAddEpicAsSubtask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic Task", "Description of epic");
        taskManager.addNewEpic(epic);

        // эпик как подзадача
        Subtask subtask = new Subtask("Subtask of Epic", "This is a subtask", TaskStatus.NEW,
                epic.getId(), Duration.ZERO, LocalDateTime.now());

        // addNewSubtask return null
        boolean isExceptionThrown = true;

        try {
            taskManager.addNewSubtask(subtask);
        } catch (IllegalArgumentException e) {
            isExceptionThrown = false; // если исключение, значит тест проходит
        }

        // Проверяем, действительно ли было выброшено исключение
        assertTrue("Эпик не должен быть добавлен как подзадача, должно быть исключение.", isExceptionThrown);
    }

    @Test
    public void testSubtaskCannotBeParentEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic Task", "Description of epic");
        taskManager.addNewEpic(epic);

        //  подзадача с другим ID
        Subtask subtask = new Subtask("Subtask", "This is a subtask", TaskStatus.NEW,
                epic.getId(), Duration.ZERO, LocalDateTime.now());
        // подзадача не получает тот же ID, что и эпик.
        subtask.setId(1);

        // добавление подзадачи вернет null
        Integer result = taskManager.addNewSubtask(subtask);

        // подзадача не добавляется
        assertNull("Подзадача не должна ссылаться на самого себя в качестве эпика.", result);
    }

    @Test
    public void testGetDefaultTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        // TaskManager не равен null
        assertNotNull("TaskManager должен быть инициализирован", taskManager);

        // TaskManager является правильным типом
        assertTrue("Должен быть экземпляр InMemoryTaskManager", taskManager instanceof InMemoryTaskManager);
    }

    @Test
    public void testGetDefaultHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        // HistoryManager не равен null
        assertNotNull("HistoryManager должен быть инициализирован", historyManager);

        // HistoryManager является правильным типом
        assertTrue("Должен быть экземпляр InMemoryHistoryManager", historyManager instanceof InMemoryHistoryManager);
    }

    @Test
    public void printAllTasks() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        // таски с разным временем
        Task task1 = new Task("Task 1", "Description of Task 1", TaskStatus.NEW, Duration.ZERO,
                LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description of Task 2", TaskStatus.NEW, Duration.ZERO,
                LocalDateTime.now().plusMinutes(1));
        manager.addNewTask(task1);
        manager.addNewTask(task2);

        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description of Epic 2");
        manager.addNewEpic(epic1);
        manager.addNewEpic(epic2);

        // сабтаски с разным временем
        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", TaskStatus.NEW,
                epic1.getId(), Duration.ZERO, LocalDateTime.now().plusMinutes(2));
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", TaskStatus.NEW,
                epic2.getId(), Duration.ZERO, LocalDateTime.now().plusMinutes(3));
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

        System.out.println("Tasks: " + manager.getTasks());
        System.out.println("Epics: " + manager.getEpics());
        System.out.println("Subtasks: " + manager.getSubtasks());

        assertEquals(2, manager.getTasks().size());
        assertEquals(2, manager.getEpics().size());
        assertEquals(2, manager.getSubtasks().size());

        assertTrue(manager.getTasks().contains(task1));
        assertTrue(manager.getTasks().contains(task2));
        assertTrue(manager.getEpics().contains(epic1));
        assertTrue(manager.getEpics().contains(epic2));
        assertTrue(manager.getSubtasks().contains(subtask1));
        assertTrue(manager.getSubtasks().contains(subtask2));
    }

    @Test
    public void testTaskTimeIntervalOverlap() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        // первая таска
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2023, 10, 1, 10, 0));
        manager.addNewTask(task1);

        // вторая таска с пересечением первой
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2023, 10, 1, 10, 30));

        // проверка что вызывается TimeException
        TimeException exception = assertThrows(TimeException.class, () -> {
            manager.addNewTask(task2);
        });

        String expectedMessage = "Пересечение по времени с другой задачей.";
        String actualMessage = exception.getMessage();

        assertTrue("Пересечение по времени с другой задачей.", actualMessage.contains(expectedMessage));
    }

    @Test
    public void testNoOverlap() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        // первая таска
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2023, 10, 1, 10, 0));
        manager.addNewTask(task1);

        // вторая таска не пересекаемая по времени
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2023, 10, 1, 11, 0));

        // проверка что таска 2 добавляется
        assertDoesNotThrow(() -> {
            manager.addNewTask(task2);
        });
    }
}
