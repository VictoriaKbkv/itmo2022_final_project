package com.itmo2022_final_project.task;

import com.itmo2022_final_project.project.Project;
import com.itmo2022_final_project.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Task findByTaskName(String name);

    Task findByTaskNameAndProject(String name, Project project);

    Set<Task> findByProjectAndIsFinished(Project project, boolean isFinished);
}
