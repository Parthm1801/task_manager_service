package com.project.task_manager_service.services;

import com.project.task_manager_service.entitiy.Task;
import com.project.task_manager_service.repositories.TaskRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public Task updateTask(Task task) {
        Task taskToUpdate = taskRepository.findById(task.getId()).orElseThrow(() -> new RuntimeException("Task not found"));
        taskToUpdate.setTitle(task.getTitle());
        taskToUpdate.setDescription(task.getDescription());
        taskToUpdate.setStatus(task.getStatus());
        taskToUpdate.setPriority(task.getPriority());
        return taskRepository.save(taskToUpdate);
    }

    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }
}
