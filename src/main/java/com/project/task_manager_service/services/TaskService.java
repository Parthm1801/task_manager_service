package com.project.task_manager_service.services;

import com.project.task_manager_service.entitiy.Task;
import com.project.task_manager_service.entitiy.User;
import com.project.task_manager_service.enums.TaskEvent;
import com.project.task_manager_service.enums.TaskStatus;
import com.project.task_manager_service.exception.ResourceNotFoundException;
import com.project.task_manager_service.repositories.TaskRepository;
import com.project.task_manager_service.repositories.UserRepository;
import com.project.task_manager_service.type.TaskDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final StateMachineFactory<TaskStatus, TaskEvent> stateMachineFactory;

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
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
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

    @Transactional
    public String updateTaskState(Long taskId, TaskEvent event) {
        try {
            Task taskToUpdate = taskRepository.findByIdWithUser(taskId)
                    .orElseThrow(() -> new RuntimeException("Task not found"));

            // Create a state machine instance
            StateMachine<TaskStatus, TaskEvent> stateMachine = getStateMachine(taskToUpdate);
            // Send event and check transition
            stateMachine.sendEvent(
                    MessageBuilder.withPayload(event)
                            .setHeader("taskId", taskId)
                            .setHeader("status", taskToUpdate.getStatus())
                            .build()
            );
            return "Task updated successfully";
        } catch (Exception ex) {
            throw new RuntimeException("Failed to update task state", ex);
        }
    }

    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }

    private StateMachine<TaskStatus, TaskEvent> getStateMachine(Task taskToUpdate) {
        StateMachine<TaskStatus, TaskEvent> stateMachine = stateMachineFactory.getStateMachine(taskToUpdate.getId().toString());
        stateMachine.stop();
        // Reset state machine to task's current status
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(accessor -> {
                    accessor.addStateMachineInterceptor(new StateMachineInterceptorAdapter<>() {
                        @Override
                        public void preStateChange(State<TaskStatus, TaskEvent> state, Message<TaskEvent> message, Transition<TaskStatus, TaskEvent> transition, StateMachine<TaskStatus, TaskEvent> stateMachine, StateMachine<TaskStatus, TaskEvent> rootStateMachine) {
                            taskToUpdate.setStatus(state.getId());
                            taskRepository.save(taskToUpdate);
                        }
                    });
                    accessor.resetStateMachine(
                            new DefaultStateMachineContext<>(taskToUpdate.getStatus(), null, null, null)
                    );
                });

        stateMachine.start();
        return stateMachine;
    }
}
