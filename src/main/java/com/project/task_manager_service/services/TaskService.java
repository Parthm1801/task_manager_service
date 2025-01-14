package com.project.task_manager_service.services;

import com.project.task_manager_service.entitiy.Task;
import com.project.task_manager_service.entitiy.User;
import com.project.task_manager_service.repositories.TaskRepository;
import com.project.task_manager_service.repositories.UserRepository;
import com.project.task_manager_service.type.TaskDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public Task createTask(Task task) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        task.setUser(user);
        return taskRepository.save(task);
    }

    public List<TaskDTO> getAllTasksForUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Task> taskList =  taskRepository.findByUserId(user.getId());
        return taskList.stream().map(task ->
                        TaskDTO.builder()
                                .title(task.getTitle())
                                .status(task.getStatus())
                                .deadline(task.getDeadline())
                                .description(task.getDescription())
                                .id(task.getId())
                                .priority(task.getPriority())
                                .build()
                ).toList();
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
