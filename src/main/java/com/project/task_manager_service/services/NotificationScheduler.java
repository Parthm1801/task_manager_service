package com.project.task_manager_service.services;

import com.project.task_manager_service.entitiy.Task;
import com.project.task_manager_service.enums.TaskStatus;
import com.project.task_manager_service.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationScheduler {
    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendOverdueNotifications() {
        Object TaskState;
        List<Task> overdueTasks = taskRepository.findAllByDeadlineBeforeAndStatusNot(LocalDateTime.now(), TaskStatus.COMPLETED);

        for (Task task : overdueTasks) {
            if (task.getStatus() != TaskStatus.OVERDUE) {
                task.setStatus(TaskStatus.OVERDUE);
                taskRepository.save(task);
            }

            String email = task.getUser().getEmail();
            String subject = "Task Overdue Notification";
            String message = "Dear " + task.getUser().getUsername() + ",\n\n" +
                    "Your task \"" + task.getTitle() + "\" with the deadline " + task.getDeadline() +
                    " is overdue. Please take immediate action.\n\n" +
                    "Best regards,\nTask Management System";

            notificationService.sendNotification(email, subject, message);
        }
    }
}
