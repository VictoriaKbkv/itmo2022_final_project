package com.itmo2022_final_project.project;

import com.itmo2022_final_project.task.Task;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ProjectCriticalPath {

    public static void CriticalPath(Set<Task> currentTasks, Set<Task> finishedTasks) {
        for(Task task:currentTasks) {
            task.setCurrentLinks(task.getPredecessors());
            if (!finishedTasks.isEmpty()) {
                Set<Task> current = task.getCurrentLinks();
                current.removeAll(finishedTasks);
                task.setCurrentLinks(current);
            }
            task.setUnhandledLinks(task.getCurrentLinks());
        }
        Integer projectDuration = ProjectCriticalPath.UpdateEarlyStartAndFinish(currentTasks);
        ProjectCriticalPath.UpdateLateStartAndFinish(currentTasks, projectDuration);
        ProjectCriticalPath.UpdateSlack(currentTasks);
        for(Task task:currentTasks) {
            if (task.getSlack() == 0) {
                task.setCritical(true);
            }
        }
    }

    public static Integer UpdateEarlyStartAndFinish(Set<Task> taskSet) {
        Integer projectDurarion = 0;
        for(Task task:taskSet) {
            task.setCurrentLinks(new HashSet<>(task.getPredecessors()));
            task.setUnhandledLinks(new HashSet<>(task.getCurrentLinks()));
        }
        Set<Task> taskToHandle = new HashSet<>();
        Set<Task> handledTasks = new HashSet<>();
        for (Task task:taskSet) {
            if (task.getCurrentLinks().size() ==0) {
                task.setEarlyStart(0);
                task.setEarlyFinish(task.getDuration());
                if (task.getEarlyFinish() > projectDurarion) {
                    projectDurarion = task.getEarlyFinish();
                }
                taskToHandle.addAll(task.getSuccessors());
                handledTasks.add(task);
            }
        }
        while (!taskToHandle.isEmpty()) {
            Set<Task> updatedTasks = new HashSet<>();
            for(Task task:taskToHandle) {
                Set<Task> current = task.getUnhandledLinks();
                current.removeAll(handledTasks);
                task.setUnhandledLinks(current);
                if (task.getUnhandledLinks().isEmpty()){
                    //Set<Integer> finishes = new HashSet<>();
                    //Set<LocalDate> starts = new HashSet<>();
                    int max = 0;
                    LocalDate start = LocalDate.now();
                    for (Task predecessor:task.getCurrentLinks()){
                        if (predecessor.getEarlyFinish() > max) {
                            max = predecessor.getEarlyFinish();
                            start = predecessor.getEndDate().plusDays(1);
                        }
                    }
                    //int max = Collections.max(finishes);
                    //LocalDate start = Collections.max(starts);
                    task.setEarlyStart(max);
                    task.setEarlyFinish(max + task.getDuration());
                    task.setStartDate(start);
                    task.setEndDate();
                    if (task.getEarlyFinish() > projectDurarion) {
                        projectDurarion = task.getEarlyFinish();
                    }
                    updatedTasks.add(task);
                }
            }
            for (Task task:updatedTasks) {
                taskToHandle.remove(task);
                taskToHandle.addAll(task.getSuccessors());
                handledTasks.add(task);
            }
            updatedTasks.clear();
        }
        return projectDurarion;
    }

    public static void UpdateLateStartAndFinish(Set<Task> taskSet, Integer projectDuration) {
        for(Task task:taskSet) {
            task.setCurrentLinks(new HashSet<>(task.getSuccessors()));
            task.setUnhandledLinks(new HashSet<>(task.getCurrentLinks()));
        }
        Set<Task> taskToHandle = new HashSet<>();
        Set<Task> handledTasks = new HashSet<>();
        for (Task task:taskSet) {
            if (task.getCurrentLinks().size() ==0) {
                task.setLateFinish(projectDuration);
                task.setLateStart(task.getLateFinish()-task.getDuration());
                taskToHandle.addAll(task.getPredecessors()); //Решить проблему с finished tasks
                handledTasks.add(task);
            }
        }
        //taskToHandle.removeAll(handledTasks);
        while (!taskToHandle.isEmpty()) {
            Set<Task> updatedTasks = new HashSet<>();
            for(Task task:taskToHandle) {
                Set<Task> current = task.getUnhandledLinks();
                current.removeAll(handledTasks);
                task.setUnhandledLinks(current);
                if (task.getUnhandledLinks().isEmpty()){
                    Set<Integer> starts = new HashSet<>();
                    for (Task successor:task.getCurrentLinks()){
                        starts.add(successor.getLateStart());
                    }
                    int min = Collections.min(starts);
                    task.setLateFinish(min);
                    task.setLateStart(min - task.getDuration());
                    updatedTasks.add(task);
                }
            }
            for (Task task:updatedTasks) {
                taskToHandle.remove(task);
                taskToHandle.addAll(task.getPredecessors()); //Решить проблему с finished tasks
                handledTasks.add(task);
            }
            updatedTasks.clear();
        }
    }

    public static void UpdateSlack(Set<Task> taskSet) {
        for(Task task: taskSet) {
            task.setSlack(task.getLateStart() - task.getEarlyStart());
        }
    }
}
