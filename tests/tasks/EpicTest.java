package tasks;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    private Epic epic;

    @BeforeEach
    public void setUp() {
        epic = new Epic("Epic Name", "Epic Description");
    }

    @Test
    public void testEpicCreation() {
        assertNotNull(epic);
        assertEquals("Epic Name", epic.getName());
        assertEquals("Epic Description", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
<<<<<<< HEAD
        assertEquals(Duration.ZERO, epic.getEpicDuration());
=======
        assertEquals(Duration.ZERO, epic.getDuration());
>>>>>>> ed885c911858f8f895bf6947c034756162fc4052
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertTrue(epic.getSubtaskIds().isEmpty(), "В новом эпике не должно быть подзадач");
    }

    @Test
    public void testSubtaskConstructor() {
        String name = "Subtask Name";
        String description = "Subtask Description";
        TaskStatus status = TaskStatus.NEW;
        int epicId = 1;
        Duration duration = Duration.ofHours(2);
        LocalDateTime startTime = LocalDateTime.now();

        Subtask subtask = new Subtask(name, description, status, epicId, duration, startTime);

        assertNotNull(subtask, "Подзадача должна быть успешно создана");
        assertEquals(name, subtask.getName(), "Имя подзадачи должно совпадать");
        assertEquals(description, subtask.getDescription(), "Описание должно совпадать");
        assertEquals(status, subtask.getStatus(), "Статус должен совпадать");
        assertEquals(epicId, subtask.getEpicId(), "ID эпика подзадачи должен совпадать");
<<<<<<< HEAD
        assertEquals(duration, subtask.getEpicDuration(), "Длительность подзадачи должна совпадать");
=======
        assertEquals(duration, subtask.getDuration(), "Длительность подзадачи должна совпадать");
>>>>>>> ed885c911858f8f895bf6947c034756162fc4052
        assertEquals(startTime, subtask.getStartTime(), "Начало подзадачи должно совпадать");
    }

    @Test
    public void testSubtaskIsAddedToEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic Name", "Epic Description");
        taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Subtask Name", "Subtask Description", TaskStatus.NEW,
                epic.getId(), Duration.ofHours(2), LocalDateTime.now());

        Integer subtaskId = taskManager.addNewSubtask(subtask);

        assertNotNull(subtaskId, "ID подзадачи не должен быть нулевым");
        assertEquals(1, epic.getSubtaskIds().size(), "После добавления в эпик должна появиться 1 подзадача");
        assertTrue(epic.getSubtaskIds().contains(subtaskId), "Эпик должен содержать id добавленной подзадачи");
    }

    @Test
    void testAddSubtaskAndCalculateTime() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic Title", "Epic Description");
        taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description for Subtask 1", TaskStatus.NEW, epic.getId(),
                Duration.ofHours(2), LocalDateTime.of(2023, 10, 1, 10, 0));
        Subtask subtask2 = new Subtask("Subtask 2", "Description for Subtask 2", TaskStatus.NEW, epic.getId(),
                Duration.ofHours(3), LocalDateTime.of(2023, 10, 2, 12, 0));

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        // Обновляем расчётные поля эпика
        epic.updateCalculatedFields(taskManager);

<<<<<<< HEAD
        assertEquals(Duration.ofHours(5), epic.getEpicDuration(), "Длительность должна быть 5 часов");
=======
        assertEquals(Duration.ofHours(5), epic.getDuration(), "Длительность должна быть 5 часов");
>>>>>>> ed885c911858f8f895bf6947c034756162fc4052
        assertEquals(LocalDateTime.of(2023, 10, 1, 10, 0),
                epic.getStartTime(), "Время начала должно совпадать с самой ранней подзадачей");
        assertEquals(LocalDateTime.of(2023, 10, 2, 15, 0),
                epic.getEndTime(), "Время окончания должно совпадать с завершением самой поздней подзадачей");
        assertEquals(List.of(subtask1.getId(), subtask2.getId()), epic.getSubtaskIds(), "ID подзадач должны совпадать");
        assertEquals(List.of(subtask1, subtask2), taskManager.getSubtasksForEpic(epic.getId()), "Подзадачи должны совпадать");
    }

    @Test
    public void testEpicStatusAllNew() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic 1", "Description of Epic 1");
        manager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", TaskStatus.NEW,
                epic.getId(), Duration.ZERO, LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", TaskStatus.NEW,
                epic.getId(), Duration.ZERO, LocalDateTime.now().plusMinutes(1));
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

        assertEquals(TaskStatus.NEW, manager.getEpicStatus(epic.getId()));
    }

    @Test
    public void testEpicStatusAllDone() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic 2", "Description of Epic 2");
        manager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", TaskStatus.DONE,
                epic.getId(), Duration.ZERO, LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", TaskStatus.DONE,
                epic.getId(), Duration.ZERO, LocalDateTime.now().plusMinutes(1));
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

        assertEquals(TaskStatus.DONE, manager.getEpicStatus(epic.getId()));
    }

    @Test
    public void testEpicStatusMixed() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic 3", "Description of Epic 3");
        manager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", TaskStatus.NEW,
                epic.getId(), Duration.ZERO, LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", TaskStatus.DONE,
                epic.getId(), Duration.ZERO, LocalDateTime.now().plusMinutes(1));
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicStatus(epic.getId()));
    }

    @Test
    public void testEpicStatusInProgress() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic 4", "Description of Epic 4");
        manager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", TaskStatus.IN_PROGRESS,
                epic.getId(), Duration.ZERO, LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", TaskStatus.IN_PROGRESS,
                epic.getId(), Duration.ZERO, LocalDateTime.now().plusMinutes(1));
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicStatus(epic.getId()));
    }
}