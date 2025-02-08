package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private String filename;
    private static int nextId = 1;

    public FileBackedTaskManager(String filename) {
        this.filename = filename;
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    public String toString(Task task) {
        return "Task{" +
                "id=" + task.getId() +
                ", name='" + task.getName() + '\'' +
                ", status='" + task.getStatus() + '\'' +
                ", description='" + task.getDescription() + '\'' +
                '}';
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",");

        if (parts.length < 5) {
            throw new IllegalArgumentException("Неправильный формат: " + value);
        }

        int id = Integer.parseInt(parts[0].trim());
        String type = parts[1].trim();
        String name = parts[2].trim();
        TaskStatus status = TaskStatus.valueOf(parts[3].trim());
        String description = parts[4].trim();
        int epicId = parts.length > 5 ? Integer.parseInt(parts[5].trim()) : 0;

        switch (type) {
            case "TASK":
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;

            case "EPIC":
                Epic epic = new Epic(name, description);
                epic.setId(id);
                return epic;

            case "SUBTASK":
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                return subtask;

            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.getAbsolutePath());

        if (!file.exists() || !file.canRead()) {
            System.out.println("Указанного файла нет или он не может быть прочитан: " + file.getAbsolutePath());
            return manager;
        }

        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                try {
                    Task task = fromString(line);
                    if (task.getId() >= nextId) {
                        nextId = task.getId() + 1;
                    }
                    if (task instanceof Epic) {
                        manager.addNewEpic((Epic) task);
                    } else if (task instanceof Subtask) {
                        manager.addNewSubtask((Subtask) task);
                    } else {
                        manager.addNewTask(task);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Пропуск строки: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка загрузки задачи из файла: " + e.getMessage());
        }

        return manager;
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename), StandardCharsets.UTF_8)) {
            for (Task task : getAllTasks()) {
                writer.write(taskToCsvString(task));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении файла: " + e.getMessage());
        }
    }

    private String taskToCsvString(Task task) {
        String type = (task instanceof Epic) ? "EPIC" : (task instanceof Subtask) ? "SUBTASK" : "TASK";
        String epicId = (task instanceof Subtask) ? String.valueOf(((Subtask) task).getEpicId()) : "";
        return String.join(",",
                String.valueOf(task.getId()),
                type,
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                epicId);
    }

    private String epicToCsvString(Epic epic) {
        return String.join(",",
                String.valueOf(epic.getId()),
                "EPIC",
                epic.getName(),
                epic.getStatus().toString(),
                epic.getDescription());
    }

    private String subtaskToCsvString(Subtask subtask) {
        return String.join(",",
                String.valueOf(subtask.getId()),
                "SUBTASK",
                subtask.getName(),
                subtask.getStatus().toString(),
                subtask.getDescription(),
                String.valueOf(subtask.getEpicId()));
    }

    private List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(getTasks());
        allTasks.addAll(getEpics());
        allTasks.addAll(getSubtasks());
        return allTasks;
    }
}

