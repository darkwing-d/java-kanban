package test;

import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static org.junit.Assert.*;

class InMemoryHistoryManagerTest {


    @Test
    public void testTaskHistoryPreservation() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Task 1", "Original description of Task 1", TaskStatus.NEW);

        historyManager.add(task1);
        List<Task> historyAfterFirstAddition = historyManager.getHistory();

        // Проверяем, что первая версия была добавлена
        assertEquals(1, historyAfterFirstAddition.size());
        assertEquals(task1.getName(), historyAfterFirstAddition.get(0).getName());

        // Обновляем задачу, создав новую версию
        Task task2 = new Task("Task 1 updated", "Updated description of Task 1", TaskStatus.NEW);
        historyManager.add(task2);

        // Получаем историю после добавления обновленной задачи
        List<Task> historyAfterSecondAddition = historyManager.getHistory();

        // Проверяем, что обе версии задачи теперь в истории
        assertEquals(2, historyAfterSecondAddition.size());

        // Проверяем, что первая версия задачи все еще доступна
        assertEquals(task1.getName(), historyAfterSecondAddition.get(0).getName());
        assertEquals(task2.getName(), historyAfterSecondAddition.get(1).getName());

        // Проверяем, что данные первой версии задачи совпадают
        assertEquals("Original description of Task 1", historyAfterSecondAddition.get(0).getDescription());

        // Проверяем данные обновленной задачи
        assertEquals("Updated description of Task 1", historyAfterSecondAddition.get(1).getDescription());
    }
}