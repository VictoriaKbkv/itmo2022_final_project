package com.itmo2022_final_project.task;

import com.itmo2022_final_project.exceptions.AppException;
import com.itmo2022_final_project.project.Project;
import com.itmo2022_final_project.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task getTaskByID(Long id) {
        return taskRepository.findById(id).get();
    }

    public Task saveNewTask(Task task) {
        if (task.getTaskName() == null) {
            throw new AppException("Task name is missing");
        }
        if (task.getStartDate() == null) {
            throw new AppException("Task start date is missing");
        }
        if (task.getDuration() == null) {
            throw new AppException("Task duration is missing");
        }
        if (task.getDuration() < 0) {
            throw new AppException("Task duration cannot be negative");
        }
        if (!(taskRepository.findByTaskNameAndProject(task.getTaskName(), task.getProject()) == null)) {
            throw new AppException("Task with name " + task.getTaskName() + " already exists");
        }
        if (task.getProgress() == null) {
            task.setProgress((short)0);
        }
        if (task.getProgress() < 0) {
            throw new AppException("Task progress cannot be negative");
        }
        if (task.getProgress() > 100) {
            throw new AppException("Task progress cannot exceed 100%");
        }
        if (task.getProgress() == 100) {
            task.setFinished(true);
        }
        task.setEndDate();
        return taskRepository.save(task);
    }

    public List<Task> getAvailableTasksToLink(Long id) {
        Task task = getTaskByID(id);
        Project project = task.getProject();
        List<Task> listTasks = project.getProjectTasks();
        listTasks.remove(task);
        for (Task t:task.getPredecessors()) {
            listTasks.remove(t);
        }
        for (Task t:task.getAllSuccessors()) {
            listTasks.remove(t);
        }
        return listTasks;
    }

    public Task saveNewTaskLink(Long id, Task taskToLink) {
        Task task = getTaskByID(id);
        String name = taskToLink.getTaskName();
        Task newPredecessor = taskRepository.findByTaskNameAndProject(name, task.getProject());
        Set<Task> predecessorSet = task.getPredecessors();
        predecessorSet.add(newPredecessor);
        task.setPredecessors(predecessorSet);
        if (task.getStartDate().isBefore(newPredecessor.getEndDate().plusDays(1))) {
            task.setStartDate(newPredecessor.getEndDate().plusDays(1));
            task.setEndDate();
            Set<Task> updatedSuccessors = task.UpdatedSuccessors();
            taskRepository.saveAll(updatedSuccessors);
        }
        return taskRepository.save(task);
    }

    public Task deleteTaskLinkByID(Long id, Long idToDelete) {
        Task task = getTaskByID(id);
        Task taskToRemove = getTaskByID(idToDelete);
        Set<Task> predecessorSet = task.getPredecessors();
        predecessorSet.remove(taskToRemove);
        task.setPredecessors(predecessorSet);
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        Task task = getTaskByID(id);
        taskRepository.delete(task);
    }

    public Task updateTask(Long taskId, Task taskInputs) {
        Task task = getTaskByID(taskId);
        if (taskInputs.getTaskName() == null) {
            throw new AppException("Task name is missing");
        }
        if (taskInputs.getStartDate() == null) {
            throw new AppException("Task start date is missing");
        }
        if (taskInputs.getDuration() == null) {
            throw new AppException("Task duration is missing");
        }
        if (taskInputs.getDuration() < 0) {
            throw new AppException("Task duration cannot be negative");
        }
        if (!task.getTaskName().equals(taskInputs.getTaskName()) && !(taskRepository.findByTaskNameAndProject(taskInputs.getTaskName(), task.getProject()) == null)) {
            throw new AppException("Task with name " + taskInputs.getTaskName() + " already exists");
        }
        if (taskInputs.getProgress() == null) {
            task.setProgress((short)0);
        }
        if (taskInputs.getProgress() < 0) {
            throw new AppException("Task progress cannot be negative");
        }
        if (taskInputs.getProgress() > 100) {
            throw new AppException("Task progress cannot exceed 100%");
        }
        Set<Task> predecessors = task.getPredecessors();
        for(Task p:predecessors) {
            if (p.getEndDate().isAfter(taskInputs.getStartDate())) {
                throw new AppException("Task cannot start before all its predecessors are finished");
            }
        }
        if (task.getProgress() == 100) {
            task.setFinished(true);
        }
        task.setTaskName(taskInputs.getTaskName());
        task.setStartDate(taskInputs.getStartDate());
        task.setDuration(taskInputs.getDuration());
        task.setProgress(taskInputs.getProgress());
        task.setEndDate();
        return taskRepository.save(task);
    }

}
