package com.itmo2022_final_project.task;

import com.itmo2022_final_project.project.Project;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "Task")
@Table(name = "tasks")
public class Task {
    @Id
    @Column(
            name = "taskId",
            updatable = false)
    @SequenceGenerator(
            name = "task_sequence",
            sequenceName = "task_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "task_sequence"
    )
    private Long taskId;
    @Column(
            name = "taskName",
            nullable = false)
    private String taskName;
    @Column(
            name = "startDate",
            nullable = false)
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate endDate;
    @Column(
            name = "duration",
            nullable = false)
    private Integer duration;
    @ManyToOne
    @JoinColumn(name = "projectId", referencedColumnName = "projectId", nullable = false)
    private Project project;
    @ManyToMany
    @JoinTable(
            name = "taskDependencies",
            joinColumns=@JoinColumn(name="taskId"),
            inverseJoinColumns=@JoinColumn(name="predecessorID")
    )
    private Set<Task> predecessors  = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "taskDependencies",
            joinColumns=@JoinColumn(name="predecessorID"),
            inverseJoinColumns=@JoinColumn(name="taskId")
    )
    private Set<Task> successors = new HashSet<>();

    @Column(name = "isCritical")
    private boolean isCritical;
    @Column(name = "isFinished")
    private boolean isFinished;
    @Column(name = "progress")
    private Short progress;
    @Transient
    private Integer earlyStart;
    @Transient
    private Integer earlyFinish;
    @Transient
    private Integer lateStart;
    @Transient
    private Integer lateFinish;
    @Column(name = "slack")
    private Integer slack;
    @Transient
    private Set<Task> currentLinks = new HashSet<>();
    @Transient
    private Set<Task> unhandledLinks = new HashSet<>();

    public Task(String taskName, LocalDate startDate, Integer duration) {
        this.taskName = taskName;
        this.startDate = startDate;
        this.duration = duration;
        this.endDate = startDate.plusDays(this.duration);

    }

    public Task() {

    }

    public Long getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        //this.endDate = startDate.plusDays(this.duration);
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setEndDate() {
        this.endDate = this.startDate.plusDays(this.duration - 1);
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Set<Task> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(Set<Task> predecessors) {
        this.predecessors = predecessors;
    }

    public Set<Task> getSuccessors() {
        return successors;
    }

    public void setSuccessors(Set<Task> successor) {
        this.successors = successor;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Integer getEarlyStart() {
        return earlyStart;
    }

    public void setEarlyStart(Integer earlyStart) {
        this.earlyStart = earlyStart;
    }

    public Integer getEarlyFinish() {
        return earlyFinish;
    }

    public void setEarlyFinish(Integer earlyFinish) {
        this.earlyFinish = earlyFinish;
    }

    public Integer getLateStart() {
        return lateStart;
    }

    public void setLateStart(Integer lateStart) {
        this.lateStart = lateStart;
    }

    public Integer getLateFinish() {
        return lateFinish;
    }

    public void setLateFinish(Integer lateFinish) {
        this.lateFinish = lateFinish;
    }

    public Integer getSlack() {
        return slack;
    }

    public void setSlack(Integer slack) {
        this.slack = slack;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public void setCritical(boolean critical) {
        isCritical = critical;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public void setProgress(Short progress) {
        this.progress = progress;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public Short getProgress() {
        return progress;
    }

    public Set<Task> getCurrentLinks() {
        return currentLinks;
    }

    public void setCurrentLinks(Set<Task> currentLinks) {
        this.currentLinks = currentLinks;
    }

    public Set<Task> getUnhandledLinks() {
        return unhandledLinks;
    }

    public void setUnhandledLinks(Set<Task> unhandledLinks) {
        this.unhandledLinks = unhandledLinks;
    }

    public Set<Task> getAllSuccessors() {
        Set<Task> result = new HashSet<>();
        if (this.successors.size() == 0) {
            return result;
        }
        result.addAll(this.successors);
        for (Task s:this.successors) {
            result.addAll(s.getAllSuccessors());
        }
        return result;
    }

    public Set<Task> getAllPredecessors() {
        Set<Task> result = new HashSet<>();
        if (this.predecessors.size() == 0) {
            return result;
        }
        result.addAll(this.predecessors);
        for (Task s:this.predecessors) {
            result.addAll(s.getAllPredecessors());
        }
        return result;
    }

    public Set<Task> UpdatedSuccessors () {
        Set<Task> result = new HashSet<>();
        if (this.successors.size() == 0) {
            return result;
        }
        for (Task s:this.successors) {
            if(s.startDate.isBefore(this.endDate)) {
                s.startDate = this.endDate.plusDays(1);
                s.setEndDate();
                result.add(s);
                result.addAll(s.UpdatedSuccessors());
            }
        }
        return result;
    }

}
