import manager.*;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import static org.junit.Assert.*;

class TaskTest {

    @Test
    void testTasksEquality() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW);

        assertNotEquals(task1, task2); // Разные id, разные задачи

        int taskId = task1.getId();
        Task task3 = new Task(task1.getName(), task1.getDescription(), task1.getStatus());

        task3.setId(taskId);

        assertEquals(task1, task3); // Задачи с одинаковыми id должны быть равны
    }

    @Test
    void testSubtaskCreation() {
        Subtask subtask = new Subtask("Subtask 1", "Description", TaskStatus.NEW, 1);
        assertEquals("Subtask 1", subtask.getName());
        assertEquals("Description", subtask.getDescription());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(1, subtask.getEpicId());
    }

    @Test
    void testEqualsAndHashCode() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description", TaskStatus.NEW, 1);
        Subtask subtask2 = new Subtask("Subtask 1", "Description", TaskStatus.NEW, 1);
        Subtask subtask3 = new Subtask("Subtask 2", "Other Description", TaskStatus.NEW, 2);

        assertEquals(subtask1, subtask2); // Они равны, потому что идентификаторы эпиков идентичны
        assertNotEquals(subtask1, subtask3); // Разные epicId
        assertEquals(subtask1.hashCode(), subtask2.hashCode()); // Хэш-коды равны
        assertNotEquals(subtask1.hashCode(), subtask3.hashCode()); // Хэш-коды разные
    }

    @Test
    public void testAddEpicAsSubtask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        // Создаем новый эпик
        Epic epic = new Epic("Epic Task", "Description of epic");
        taskManager.addNewEpic(epic);

        // Пытаемся добавить эпик как подзадачу
        Subtask subtask = new Subtask("Subtask of Epic", "This is a subtask", TaskStatus.NEW, epic.getId());

        // Проверяем, что метод addNewSubtask возвращает null
        boolean isExceptionThrown = true;

        // Метод, вызывающий добавление подзадачи
        try {
            taskManager.addNewSubtask(subtask);
        } catch (IllegalArgumentException e) {
            isExceptionThrown = false; // Если исключение было выброшено, значит, тест проходит
        }

        // Проверяем, действительно ли было выброшено исключение
        assertTrue("Эпик не должен быть добавлен как подзадача, ожидалось исключение.", isExceptionThrown);
    }

    @Test
    public void testSubtaskCannotBeParentEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic Task", "Description of epic");
        taskManager.addNewEpic(epic);

        // создаем подзадачу с другим ID
        Subtask subtask = new Subtask("Subtask", "This is a subtask", TaskStatus.NEW, epic.getId());
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

        Task task1 = new Task("Task 1", "Description of Task 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description of Task 2", TaskStatus.NEW);
        manager.addNewTask(task1);
        manager.addNewTask(task2);

        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description of Epic 2");
        manager.addNewEpic(epic1);
        manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", TaskStatus.NEW, epic2.getId());
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
}
