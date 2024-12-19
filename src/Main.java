/*
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
    private static Task epic1;

    public static void main(String[] args) {
        TasksManager manager = new TasksManager();
        Epic epic = new Epic();
        Scanner scanner = new Scanner(System.in);
        Test();

        while (true) {
            System.out.println("Создание");
            System.out.println("1. Задача");
            System.out.println("2. Эпик");
            System.out.println("3. подзадача");
            System.out.println("-----------------------------");
            System.out.println("Получение");
            System.out.println("4. Список всех задач");
            System.out.println("5. Список всех эпиков");
            System.out.println("6. Список всех подзадач");
            System.out.println("-----------------------------");
            System.out.println("Удаление всех задач");
            System.out.println("7. Удаление всех задач");
            System.out.println("8. удаление всех эпиков(и их подзадач)");
            System.out.println("9. удаление всех подзадач");
            System.out.println("20. удалить всё");
            System.out.println("-----------------------------");
            System.out.println("Получение по идентификатору");
            System.out.println("10. Получение задач");
            System.out.println("11. получение эпиков");
            System.out.println("12. получение подзадач");
            System.out.println("-----------------------------");
            System.out.println("Обновление");
            System.out.println("13. Обновить задачу");
            System.out.println("14. Обновить эпик");
            System.out.println("15. Обновить подзадачу");
            System.out.println("-----------------------------");
            System.out.println("Удаление по идентификатору");
            System.out.println("16. удаление задач");
            System.out.println("17. удаление эпиков");
            System.out.println("18. удаление подзадач");
            System.out.println("-----------------------------");
            System.out.println("19. Получение всех задач определенного эпика");


            int create = scanner.nextInt();

            if (create == 1) {
                System.out.println("name:");
                String name = scanner.next();
                System.out.println("description:");
                String description = scanner.next();
                //  System.out.println("status:");
                //  String status = scanner.next();

                Task task = new Task(name, description, TaskStatus.NEW);
                manager.addNewTask(task);
                //    System.out.println("name: " + name + ", description: " + description + ", status: " + status + ", id: " + manager.addNewTask(task));

            } else if (create == 2) {
                System.out.println("name:");
                String name = scanner.next();
                System.out.println("description:");
                String description = scanner.next();
                //   System.out.println("status:");
                //   String status = scanner.next();

                Epic epics = new Epic(name, description, TaskStatus.NEW);
                manager.addNewEpic(epics);
                //  System.out.println("name: " + name + ", description: " + description + ", status: " + status + ", id: " + manager.addNewEpic(epics));

            } else if (create == 3) {
                System.out.println("in Epic id? :");
                int idEpic = scanner.nextInt();
                System.out.println("name:");
                String name = scanner.next();
                System.out.println("description:");
                String description = scanner.next();
                //  System.out.println("status:");
                //  String status = scanner.next();
                Subtask subtask = new Subtask(name, description, TaskStatus.NEW, idEpic);
                manager.addNewSubtask(subtask);
                //     System.out.println("name: " + name +
                //             ", description: " + description +
                //             ", status: " + status +
                //             ", id: " + manager.getGeneratorId() +
                //             ", idEpic: " + idEpic +
                //             ", Epic name:" + manager.getEpic(idEpic) +
                //             manager.addNewSubtask(subtask));

            } else if (create == 4) {
                //    System.out.println(manager.getTasks());
                manager.getTasks();
            } else if (create == 5) {
                //    System.out.println(manager.getEpics());
                manager.getEpics();
            } else if (create == 6) {
                //   System.out.println(manager.getSubtasks());
                manager.getSubtasks(epic1.getId());

            } else if (create == 7) {
                manager.deleteTasks();
            } else if (create == 8) {
                manager.deleteEpics();
            } else if (create == 9) {
                epic.cleanSubtaskIds();
            } else if (create == 20) {
                manager.deleteTasks();
                manager.deleteEpics();
                epic.cleanSubtaskIds();


            } else if (create == 10) {
                System.out.println("id:");
                int taskId = scanner.nextInt();
                manager.searchIdTasks(taskId);
                //  System.out.println(manager.searchIdTasks(taskId));
            } else if (create == 11) {
                System.out.println("id:");
                int taskEpicId = scanner.nextInt();
                manager.searchIdEpics(taskEpicId);
                //   System.out.println(manager.searchIdEpics(taskEpicId));
            } else if (create == 12) {
                System.out.println("id:");
                int taskSubId = scanner.nextInt();
                manager.searchIdSubtasks(taskSubId);
                //  System.out.println(manager.searchIdSubtasks(taskSubId));


            } else if (create == 13) {
                System.out.println("id: ");
                int taskId = scanner.nextInt();

                System.out.println("name:");
                String name = scanner.next();

                System.out.println("description:");
                String description = scanner.next();

                manager.updateTask(taskId, name, description);
            } else if (create == 14) {
                System.out.println("id: ");
                int taskEpicId = scanner.nextInt();

                System.out.println("name:");
                String name = scanner.next();

                System.out.println("description:");
                String description = scanner.next();

                manager.updateEpic(taskEpicId, name, description);
            } else if (create == 15) {
                System.out.println("id epic: ");
                int taskEpicId = scanner.nextInt();

                System.out.println("name:");
                String name = scanner.next();

                System.out.println("description:");
                String description = scanner.next();

                manager.updateSubtask(taskEpicId, name, description);

            } else if (create == 16) {
                System.out.println("id task:");
                int id = scanner.nextInt();
                manager.deleteIdTask(id);

            } else if (create == 17) {
                System.out.println("id epic:");
                int id = scanner.nextInt();
                manager.deleteIdEpic(id);

            } else if (create == 18) {
                System.out.println("id subtask:");
                int id = scanner.nextInt();
                manager.deleteIdSubtask(id);

            } else if (create == 19) {
                System.out.println("id epic:");
                int id = scanner.nextInt();
                manager.getEpicsSubtasks(id);
                //  System.out.println(manager.getEpiscSubtasks(id));
            }
        }
    }

    public static void Test() {
        testGetSubtasksByEpicId();
        testAddTaskAndEpic();
        testGetAllTasksEpicsAndSubtasks();
        testDeleteAllTasksEpics();
        testDeleteAllSubtasks();
        testDeleteTaskEpic();
        testEpicCreation();
        testEpicIdIncrement();
        testSearchEpicId();
        testSearchSubtaskId();
        testUpdate();
    }


    private static void testGetSubtasksByEpicId() {
        TasksManager taskManager = new TasksManager();
        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description of Epic 2");

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", epic1.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description of Subtask 3", epic2.getId());

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        // Получаем подзадачи для epic1
        List<Subtask> subtasksForEpic1 = taskManager.getSubtasks(epic1.getId());

        // Проверяем, что возвращается правильное количество подзадач
        if (subtasksForEpic1.size() == 3) {
            System.out.println("testGetSubtasksByEpicId passed.");
        } else {
            System.out.println("testGetSubtasksByEpicId failed.");
        }

        // Проверяем, что возвращаемые подзадачи соответствуют ожидаемым
        if (subtasksForEpic1.contains(subtask1) && subtasksForEpic1.contains(subtask2)) {
            System.out.println("testGetSubtasksByEpicId contains correct subtasks: passed.");
        } else {
            System.out.println("testGetSubtasksByEpicId contains correct subtasks: failed.");
        }
    }

    private static void testAddTaskAndEpic() {
        TasksManager taskManager = new TasksManager();

        // Создаем и добавляем эпик
        Epic epic = new Epic("Epic 1", "Description of Epic 1");
        taskManager.addNewEpic(epic);

        // Проверяем, что эпик был успешно добавлен
        Epic retrievedEpic = taskManager.getEpic(epic.getId());
        if (retrievedEpic != null && retrievedEpic.getName().equals(epic.getName())) {
            System.out.println("testAddEpic passed.");
        } else {
            System.out.println("testAddEpic failed.");
        }

        // Создаем и добавляем подзадачи
        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", epic.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        // Проверяем, что подзадачи были успешно добавлены
        List<Subtask> retrievedSubtasks = taskManager.getSubtasks(epic.getId());
        if (retrievedSubtasks.size() == 2 &&
                retrievedSubtasks.contains(subtask1) &&
                retrievedSubtasks.contains(subtask2)) {

            System.out.println("testAddSubtasks passed.");
        } else {
            System.out.println("testAddSubtasks failed.");
        }
    }

    private static void testGetAllTasksEpicsAndSubtasks() {
        TasksManager taskManager = new TasksManager();

        // Создаем задачи, эпики и подзадачи
        Task task1 = new Task("Task 1", "Description of Task 1");
        Task task2 = new Task("Task 2", "Description of Task 2");
        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", epic1.getId());

        // Добавляем задачи и эпики
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        // Получаем списки
        List<Task> tasks = taskManager.getTasks();
        List<Epic> epics = taskManager.getEpics();
        List<Subtask> subtasks = taskManager.getSubtasks(epic1.getId());

        // Проверка наличия задач
        if (tasks.size() == 2 && tasks.contains(task1) && tasks.contains(task2)) {
            System.out.println("testGetAllTasks passed.");
        } else {
            System.out.println("testGetAllTasks failed.");
        }

        // Проверка наличия эпиков
        if (epics.size() == 1 && epics.contains(epic1)) {
            System.out.println("testGetAllEpics passed.");
        } else {
            System.out.println("testGetAllEpics failed.");
        }

        // Проверка наличия подзадач
        if (subtasks.size() == 2 && subtasks.contains(subtask1) && subtasks.contains(subtask2)) {
            System.out.println("testGetAllSubtasks passed.");
        } else {
            System.out.println("testGetAllSubtasks failed.");
        }
    }

    private static void testDeleteAllTasksEpics() {
        TasksManager taskManager = new TasksManager();

        // Создание задач, эпиков и подзадач
        Task task1 = new Task("Task 1", "Description of Task 1");
        Task task2 = new Task("Task 2", "Description of Task 2");
        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", epic1.getId());

        // Добавление задач, эпиков и подзадач
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        // Удаление всех задач
        taskManager.deleteAllTasks();

        // Проверка, что список задач пуст
        ArrayList<Task> tasks = taskManager.getTasks();
        if (tasks.isEmpty()) {
            System.out.println("testDeleteAllTasks passed.");
        } else {
            System.out.println("testDeleteAllTasks failed.");
        }

        // Удаление всех эпиков (с подзадачами)
        taskManager.deleteEpics();

        // Проверка, что список эпиков пуст
        ArrayList<Epic> epics = taskManager.getEpics();
        if (epics.isEmpty()) {
            System.out.println("testDeleteAllEpics passed.");
        } else {
            System.out.println("testDeleteAllEpics failed.");
        }
    }

    private static void testDeleteAllSubtasks() {
        // добавление задач и эпиков для дальнейших тестов
        TasksManager taskManager = new TasksManager();
        Task task1 = new Task("Task 1", "Description of Task 1");
        Task task2 = new Task("Task 2", "Description of Task 2");
        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", epic1.getId());

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        // Удаление отдельной подзадачи
        taskManager.deleteIdSubtask(subtask1.getId());

        // Проверка, что подзадача удалена
        ArrayList<Subtask> subtasks = taskManager.getSubtasks(epic1.getId());
        if (subtasks.size() == 1 && !subtasks.contains(subtask1)) {
            System.out.println("testDeleteSubtask passed.");
        } else {
            System.out.println("testDeleteSubtask failed.");
        }
    }

    private static void testDeleteTaskEpic() {
        TasksManager taskManager = new TasksManager();

        // Создаем задачи и эпики
        Task task1 = new Task("Task 1", "Description of task 1", TaskStatus.NEW);
        Epic epic1 = new Epic("Epic Task 1", "Description of epic task 1", TaskStatus.NEW);

        // Добавляем задачи и эпики и получаем их id
        int taskId1 = taskManager.addNewTask(task1);
        int epicId1 = taskManager.addNewEpic(epic1);

        // Проверяем наличие задач и эпиков до удаления
        System.out.println("Before deletion:");
        System.out.println("Task with ID " + taskId1 + ": " + taskManager.searchIdTasks(taskId1));
        System.out.println("Epic with ID " + epicId1 + ": " + taskManager.getEpic(epicId1));

        // Удаляем задачу и эпик
        boolean taskDeleted = taskManager.deleteIdTask(taskId1);
        boolean epicDeleted = taskManager.deleteIdEpic(epicId1);

        // Проверяем результат удаления
        System.out.println("\nAfter deletion:");
        System.out.println("Task with ID " + taskId1 + " deleted: " + taskDeleted);
        System.out.println("Task after deletion: " + taskManager.deleteIdTask(taskId1));
        System.out.println("Epic with ID " + epicId1 + " deleted: " + epicDeleted);
        System.out.println("Epic after deletion: " + taskManager.getEpic(epicId1));


    }

    public static void testEpicCreation() {
        Epic epic = new Epic("Epic Task", "Description of epic task", TaskStatus.NEW);

        // Проверяем, что имя установлено правильно
        if (!epic.getName().equals("Epic Task")) {
            System.out.println("testEpicCreation failed: Name mismatch.");
        }

        // Проверяем, что описание установлено правильно
        if (!epic.getDescription().equals("Description of epic task")) {
            System.out.println("testEpicCreation failed: Description mismatch.");
        }

        // Проверяем, что статус установлен правильно
        if (epic.getStatus() != TaskStatus.NEW) {
            System.out.println("testEpicCreation failed: Status mismatch.");
        }

        System.out.println("testEpicCreation passed");
    }

    public static void testEpicIdIncrement() {
        TasksManager tasksManager = new TasksManager();

        // Добавление задач
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW);
        tasksManager.addNewTask(task1);
        tasksManager.addNewTask(task2);

        // Проверка уникальности ID задач
        System.out.println("Task 1 ID: " + task1.getId()); // Ожидаемо ID 1
        System.out.println("Task 2 ID: " + task2.getId()); // Ожидаемо ID 2
        System.out.println("Tasks are unique: " + (task1.getId() != task2.getId())); // Ожидаемо true

        // Добавление эпиков
        Epic epic1 = new Epic("Epic 1", "Description 1", TaskStatus.NEW);
        Epic epic2 = new Epic("Epic 2", "Description 2", TaskStatus.IN_PROGRESS);
        tasksManager.addNewEpic(epic1);
        tasksManager.addNewEpic(epic2);

        // Проверка уникальности ID эпиков
        System.out.println("Epic 1 ID: " + epic1.getId()); // Ожидаемо ID 3
        System.out.println("Epic 2 ID: " + epic2.getId()); // Ожидаемо ID 4
        System.out.println("Epics are unique: " + (epic1.getId() != epic2.getId())); // Ожидаемо true

        // Проверка последовательности ID
        System.out.println("Task ID 1 should be 1: " + (task1.getId() == 1)); // Ожидаемо true
        System.out.println("Task ID 2 should be 2: " + (task2.getId() == 2)); // Ожидаемо true
        System.out.println("Epic ID 1 should be 3: " + (epic1.getId() == 3)); // Ожидаемо true
        System.out.println("Epic ID 2 should be 4: " + (epic2.getId() == 4)); // Ожидаемо true

        // Проверка количества задач и эпиков
        System.out.println("Total tasks: " + tasksManager.getTaskCount()); // Ожидаемо 2
        System.out.println("Total epics: " + tasksManager.getEpicCount()); // Ожидаемо 2
    }

    public static void testSearchEpicId() {
        TasksManager tasksManager = new TasksManager();

        // Добавление эпиков
        Epic epic1 = new Epic("Epic 1", "Description 1", TaskStatus.NEW);
        Epic epic2 = new Epic("Epic 2", "Description 2", TaskStatus.IN_PROGRESS);
        tasksManager.addNewEpic(epic1);
        tasksManager.addNewEpic(epic2);

        // Тест: Поиск эпика по ID
        System.out.println("Searching for Epic with ID 1:");
        Epic foundEpic = tasksManager.searchIdEpics(epic1.getId());

        // Проверка, найден ли корректный эпик
        if (foundEpic != null && foundEpic.getId() == epic1.getId()) {
            System.out.println("Epic found: " + "ID: " + foundEpic.getId() + ", Name: " + "Epic 1");
        } else {
            System.out.println("Epic not found!");
        }

        // Тест: Поиск эпика по некорректному ID
        System.out.println("Searching for Epic with ID 3:");
        foundEpic = tasksManager.searchIdEpics(3); // Эпика с ID 3 не существует

        if (foundEpic == null) {
            System.out.println("Epic not found! As expected.");
        } else {
            System.out.println("Unexpectedly found Epic: " + foundEpic.getId());
        }
    }

    public static void testSearchSubtaskId() {
        TasksManager tasksManager = new TasksManager();

        // Добавление эпика
        Epic epic1 = new Epic("Epic 1", "Description of Epic 1", TaskStatus.NEW);
        tasksManager.addNewEpic(epic1);

        // Добавление подзадач
        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", TaskStatus.NEW, epic1.getId());
        tasksManager.addNewSubtask(subtask1);
        tasksManager.addNewSubtask(subtask2);

        // Поиск подзадачи по ID
        System.out.println("Searching for Subtask with ID 1:");
        Subtask foundSubtask = tasksManager.searchIdSubtasks(subtask1.getId());

        if (foundSubtask != null) {
            System.out.println("Subtask found: ID: " + foundSubtask.getId() + ", Name: " + "Subtask 1");
        } else {
            System.out.println("Subtask not found!");
        }

        // Поиск подзадачи по некорректному ID
        System.out.println("Searching for Subtask with ID 3:");
        foundSubtask = tasksManager.searchIdSubtasks(3);

        if (foundSubtask == null) {
            System.out.println("Subtask not found! As expected.");
        }
    }

    public static void testUpdate() {
        TasksManager tasksManager = new TasksManager();

        // Создаем задачи, эпики и подзадачи
        Task task1 = new Task("Task 1", "Description of Task 1", TaskStatus.NEW);
        Epic epic1 = new Epic("Epic 1", "Description of Epic 1", TaskStatus.NEW);
        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", TaskStatus.NEW, epic1.getId());

        tasksManager.addNewTask(task1);
        tasksManager.addNewEpic(epic1);
        tasksManager.addNewSubtask(subtask1);

// Проверка начальных значений
        System.out.println("Initial values:");
        System.out.println("Task: " + task1.getName());
        System.out.println("Epic: " + epic1.getName());
        System.out.println("Subtask: " + subtask1.getName());

        // Обновление задачи
        boolean isTaskUpdated = tasksManager.updateTask(task1.getId(), "Updated Task 1", "Updated Description");
        System.out.println("Task updated: " + isTaskUpdated);

        // Обновление эпика
        boolean isEpicUpdated = tasksManager.updateEpic(epic1.getId(), "Updated Epic 1", "Updated Description");
        System.out.println("Epic updated: " + isEpicUpdated);

        // Обновление подзадачи
        boolean isSubtaskUpdated = tasksManager.updateSubtask(subtask1.getId(), "Updated Subtask 1", "Updated Subtask Description");
        System.out.println("Subtask updated: " + isSubtaskUpdated);

        // Проверка обновленных значений
        System.out.println("\nUpdated values:");
        System.out.println("Task: " + task1.getName());
        System.out.println("Epic: " + epic1.getName());
        System.out.println("Subtask: " + subtask1.getName());

        // Проверка, что обновление производится корректно
        if (tasksManager.searchIdTasks(task1.getId()) != null) {
            System.out.println("Updated Task: " + tasksManager.searchIdTasks(task1.getId()).getName());
        }
        if (tasksManager.searchIdEpics(epic1.getId()) != null) {
            System.out.println("Updated Epic: " + tasksManager.searchIdEpics(epic1.getId()).getName());
        }
        if (tasksManager.searchIdSubtasks(subtask1.getId()) != null) {
            System.out.println("Updated Subtask: " + tasksManager.searchIdSubtasks(subtask1.getId()).getName());
        }
    }

}
*/
