package com.project.task_manager_service.controller;

import com.project.task_manager_service.entitiy.Task;
import com.project.task_manager_service.enums.TaskEvent;
import com.project.task_manager_service.services.TaskService;
import com.project.task_manager_service.type.TaskDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(task));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasksForUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PatchMapping
    public ResponseEntity<Task> updateTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.updateTask(task));
    }

    @PatchMapping("update-task/{id}/{event}")
    public ResponseEntity<?> updateTaskById(@PathVariable Long id, @PathVariable TaskEvent event) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(taskService.updateTaskState(id, event));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
