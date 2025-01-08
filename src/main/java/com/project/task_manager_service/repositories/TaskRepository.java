package com.project.task_manager_service.repositories;

import com.project.task_manager_service.entitiy.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
