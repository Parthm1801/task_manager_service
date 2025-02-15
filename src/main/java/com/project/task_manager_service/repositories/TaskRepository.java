package com.project.task_manager_service.repositories;

import com.project.task_manager_service.entitiy.Task;
import com.project.task_manager_service.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    @Query("SELECT t FROM Task t JOIN FETCH t.user WHERE t.id = :taskId")
    Optional<Task> findByIdWithUser(@Param("taskId") Long taskId);
    List<Task> findAllByDeadlineBeforeAndStatusNot(LocalDateTime deadline, TaskStatus status);
}
