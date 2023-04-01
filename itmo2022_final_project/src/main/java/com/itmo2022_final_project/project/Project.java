package com.itmo2022_final_project.project;

import com.itmo2022_final_project.task.Task;
import com.itmo2022_final_project.user.User;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "project")
@Table(name = "projects")
public class Project {
    @Id
    @Column(
            name = "projectID",
            updatable = false)
    @SequenceGenerator(
            name = "project_sequence",
            sequenceName = "project_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "project_sequence"
    )
    private Long projectId;
    @Column(
            name = "projectName",
            nullable = false)
    private String name;
    @Column(name = "dueDate")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dueDate;

    @Column(name = "startDate")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name = "finishDate")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate finishDate;

    @ManyToOne
    @JoinColumn(name = "userID", referencedColumnName = "userID", nullable = false)
    private User user;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Task> projectTasks = new ArrayList<Task>();
    @Column(name = "onTrack")
    private boolean onTrack;

    public Long getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public List<Task> getProjectTasks() {
        return projectTasks;
    }

    public void setProjectTasks(List<Task> projectTasks) {
        this.projectTasks = projectTasks;
    }

    public boolean isOnTrack() {
        return onTrack;
    }

    public void setOnTrack(boolean onTrack) {
        this.onTrack = onTrack;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDate finishDate) {
        this.finishDate = finishDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void UpdateFinishDate() {
        LocalDate newFinishDate = this.getStartDate();
        for(Task t:this.getProjectTasks()) {
            if (t.getEndDate().isAfter(newFinishDate)) {
                newFinishDate = t.getEndDate();
            }
        }
        this.setFinishDate(newFinishDate);
        if (this.getFinishDate().isAfter(this.getDueDate())) {
            this.setOnTrack(false);
        }
    }
}
