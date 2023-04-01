package com.itmo2022_final_project.project;

import com.itmo2022_final_project.exceptions.AppException;
import com.itmo2022_final_project.task.Task;
import com.itmo2022_final_project.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    public Project saveNewProject(Project project) {
        if (project.getName() == null) {
            throw new AppException("Project name is missing");
        }
        if (project.getDueDate().isBefore(LocalDate.now())) {
            throw new AppException("Project due date is before current date");
        }
        project.setOnTrack(true);
        if (project.getStartDate() == null) {
            LocalDate date = LocalDate.now();
            project.setStartDate(date);
        }
        project.setFinishDate(project.getDueDate());
        return projectRepository.save(project);
    }

    public Project getProjectByID(Long id) {
        return projectRepository.findById(id).get();
    }

    public Project UpdateProject(Long id) {
        Project project = getProjectByID(id);
        Set<Task> currentTasks = taskRepository.findByProjectAndIsFinished(project, false);
        Set<Task> finishedTasks = taskRepository.findByProjectAndIsFinished(project, true);
        ProjectCriticalPath.CriticalPath(currentTasks, finishedTasks);
        project.UpdateFinishDate();
        return projectRepository.save(project);
    }

    public Project UpdateDates(Task task) {
        Project project = task.getProject();
        if (project.getStartDate().isAfter(task.getStartDate())) {
            project.setStartDate(task.getStartDate());
        }
        if (project.getFinishDate().isBefore(task.getEndDate())) {
            project.setFinishDate(task.getEndDate());
        }
        if (project.getFinishDate().isAfter(project.getDueDate())) {
            project.setOnTrack(false);
        }
        return projectRepository.save(project);
    }

    public Project UpdateDatesIfTaskIsDeleted(Long id) {
        Project project = getProjectByID(id);
        project.UpdateFinishDate();
        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        Project project = getProjectByID(id);
        projectRepository.delete(project);
    }
}
