package task;

import com.google.gson.annotations.SerializedName;
import manager.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();
    private Duration epicDuration; // Сумма продолжительностей подзадач
    @SerializedName("epicStartTime")
    private LocalDateTime startTime; // поле для JSON
    private LocalDateTime endTime; // Время завершения самой поздней подзадачи

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW, Duration.ZERO, null); // статус эпика при создании
        this.epicDuration = Duration.ZERO; // Начальная продолжительность
        this.startTime = null; // Начальное значение для startTime
        this.endTime = null; // Начальное значение для endTime
    }

    public void updateCalculatedFields(InMemoryTaskManager taskManager) {
        epicDuration = Duration.ZERO;
        startTime = null;
        endTime = null;

        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = taskManager.getSubtask(subtaskId);
            if (subtask != null) {
                epicDuration = epicDuration.plus(subtask.getEpicDuration());

                if (startTime == null || subtask.getStartTime().isBefore(startTime)) {
                    startTime = subtask.getStartTime();
                }

                if (endTime == null || subtask.getEndTime().isAfter(endTime)) {
                    endTime = subtask.getEndTime();
                }
            }
        }
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public Duration getEpicDuration() {
        return epicDuration;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void clearSubtasks() {
        subtaskIds.clear();
        // Обнуляем данные эпика, если подзадач больше нет
        epicDuration = Duration.ZERO;
        startTime = null;
        endTime = null;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Epic epic = (Epic) object;
        return Objects.equals(subtaskIds, epic.subtaskIds) &&
                Objects.equals(epicDuration, epic.epicDuration) &&
                Objects.equals(startTime, epic.startTime) &&
                Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds, epicDuration, startTime, endTime);
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void setEpicDuration(Duration epicDuration) {
        this.epicDuration = epicDuration;
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}