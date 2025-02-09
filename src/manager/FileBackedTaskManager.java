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
    public void deleteSubtask(int subtaskId) {
        super.deleteSubtask(subtaskId);
        save();
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
            throw new TaskFormatException("Неправильный формат: " + value + ". Ожидалось минимум 5 частей.");
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
        int epicId = (parts.length > 5) ? Integer.parseInt(parts[5].trim()) : 0;

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
                throw new TaskFormatException("Неизвестный тип задачи: " + type);
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

    public void save() throws FileSaveException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename), StandardCharsets.UTF_8)) {
            for (Task task : getAllTasks()) {
                writer.write(taskToCsvString(task));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FileSaveException("Ошибка при сохранении файла: " + e.getMessage(), e);
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

