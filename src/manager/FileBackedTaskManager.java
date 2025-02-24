package manager;

import exceptions.FileSaveException;
import exceptions.TaskFormatException;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private String filename;

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

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public boolean updateTask(Task task) {
        boolean updated = super.updateTask(task);
        if (updated) {
            save();
        }
        return updated;
    }

    @Override
    public boolean updateEpic(Epic epicToUpdate) {
        boolean updated = super.updateEpic(epicToUpdate);
        if (updated) {
            save();
        }
        return updated;
    }

    @Override
    public boolean updateSubtask(Subtask updatedSubtask) {
        boolean updated = super.updateSubtask(updatedSubtask);
        if (updated) {
            save();
        }
        return updated;
    }

    @Override
    public boolean deleteEpic(int id) {
        boolean deleted = super.deleteEpic(id);
        if (deleted) {
            save();
        }
        return deleted;
    }

    @Override
    public boolean deleteTask(int taskId) {
        boolean deleted = super.deleteTask(taskId);
        if (deleted) {
            save();
        }
        return deleted;
    }

    @Override
    public boolean deleteSubtask(int subtaskId) {
        boolean deleted = super.deleteSubtask(subtaskId);
        if (deleted) {
            save();
        }
        return deleted;
    }

    public String toString(Task task) {
        return "Task{" +
                "id=" + task.getId() +
                ", name='" + task.getName() + '\'' +
                ", status='" + task.getStatus() + '\'' +
                ", description='" + task.getDescription() + '\'' +
                '}';
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
                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.addNewEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    manager.addNewSubtask((Subtask) task);
                } else {
                    manager.addNewTask(task);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка загрузки задачи из файла: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка при обработке задачи: " + e.getMessage());
        }
        return manager;
    }

    public void saveTasks() {
        save(); // Вызов приватного метода внутри публичного метода для прохождения теста
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename), StandardCharsets.UTF_8)) {
            for (Task task : getAllTasks()) {
                writer.write(taskToCsvString(task));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FileSaveException("Ошибка при сохранении файла: " + e.getMessage(), e);
        }
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");

        if (parts.length < 6) {
            throw new TaskFormatException("Неправильный формат: " + value + ". Ожидалось минимум 6 частей.");
        }

        int id;
        try {
            id = Integer.parseInt(parts[0].trim());
        } catch (NumberFormatException e) {
            throw new TaskFormatException("Неправильный ID задачи: " + parts[0].trim());
        }

        String type = parts[1].trim();
        String name = parts[2].trim();
        TaskStatus status;
        try {
            status = TaskStatus.valueOf(parts[3].trim());
        } catch (IllegalArgumentException e) {
            throw new TaskFormatException("Неизвестный статус задачи: " + parts[3].trim());
        }
        String description = parts[4].trim();

        Duration duration;
        try {
            duration = Duration.parse(parts[5].trim());
        } catch (DateTimeParseException e) {
            throw new TaskFormatException("Неправильная продолжительность задачи: " + parts[5].trim());
        }

        LocalDateTime startTime = null;
        if (parts.length > 6 && !parts[6].trim().isEmpty()) {
            try {
                startTime = LocalDateTime.parse(parts[6].trim());
            } catch (Exception e) {
                throw new TaskFormatException("Неправильное время начала задачи: " + parts[6].trim());
            }
        }

        int epicId = (parts.length > 7) ? Integer.parseInt(parts[7].trim()) : 0; // Эпик ID для подзадач

        switch (type) {
            case "TASK":
                Task task = new Task(name, description, status, duration, startTime);
                task.setId(id);
                return task;

            case "EPIC":
                Epic epic = new Epic(name, description);
                epic.setId(id);
                return epic;

            case "SUBTASK":
                Subtask subtask = new Subtask(name, description, status, epicId, duration, startTime);
                subtask.setId(id);
                return subtask;

            default:
                throw new TaskFormatException("Неизвестный тип задачи: " + type);
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
                task.getDuration() != null ? task.getDuration().toString() : "",
                task.getStartTime() != null ? task.getStartTime().toString() : "",
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
                subtask.getDuration() != null ? subtask.getDuration().toString() : "",
                subtask.getStartTime() != null ? subtask.getStartTime().toString() : "",
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

