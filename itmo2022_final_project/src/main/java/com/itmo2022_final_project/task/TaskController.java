package com.itmo2022_final_project.task;

import com.itmo2022_final_project.exceptions.AppException;
import com.itmo2022_final_project.task.Task;
import com.itmo2022_final_project.task.TaskService;
import com.itmo2022_final_project.project.Project;
import com.itmo2022_final_project.project.ProjectService;
import com.itmo2022_final_project.user.AppUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/task")
public class TaskController {

    private final TaskService taskService;
    private final ProjectService projectService;
   @Autowired
    public TaskController(TaskService taskService, ProjectService projectService) {
        this.taskService = taskService;
        this.projectService = projectService;
    }

    @PostMapping("/add")
    public String saveNewTask(@ModelAttribute("task") Task task, Model model) {
        taskService.saveNewTask(task);
        projectService.UpdateDates(task);
        model.addAttribute("project", task.getProject());
        return "edit_project";
    }

    @GetMapping("/edit-links/{id}")
    public String editTaskLinksForm(@PathVariable Long id, Model model) {
        model.addAttribute("task", taskService.getTaskByID(id));
        model.addAttribute("listTasks", taskService.getAvailableTasksToLink(id));
        return "edit_task_links";
    }

    @PostMapping("/{id}/link/add")
    public String saveNewTaskLink(@PathVariable Long id, @ModelAttribute("task") Task taskToLink, Model model, Authentication authentication) {
       CheckAuthorization(taskService.getTaskByID(id), authentication);
       if (taskToLink.getTaskName() == null) {
           model.addAttribute("task", taskService.getTaskByID(id));
           return "edit_task_links";
        }
        Task task = taskService.saveNewTaskLink(id, taskToLink);
        projectService.UpdateDates(task);
        model.addAttribute("task", taskService.getTaskByID(id));
        model.addAttribute("listTasks", taskService.getAvailableTasksToLink(id));
        return "edit_task_links";
    }

    @GetMapping("/{id}/delete-link/{del_id}")
    public String deleteTaskLink(@PathVariable("id") Long id, @PathVariable("del_id") Long idToDelete, Model model, Authentication authentication) {
        CheckAuthorization(taskService.getTaskByID(id), authentication);
        Task updatedTask = taskService.deleteTaskLinkByID(id, idToDelete);
        model.addAttribute("task", updatedTask);
        model.addAttribute("listTasks", taskService.getAvailableTasksToLink(updatedTask.getTaskId()));
        return "edit_task_links";
    }

    @GetMapping("/delete/{id}/project/{project_id}")
    public String deleteTask(@PathVariable("id") Long id, @PathVariable("project_id") Long projectId, Authentication authentication){
        CheckAuthorization(taskService.getTaskByID(id), authentication);
        CheckProjectAuthorization(projectService.getProjectByID(projectId), authentication);
        taskService.deleteTask(id);
        projectService.UpdateDatesIfTaskIsDeleted(projectId);
        return "redirect:/project/edit/{project_id}";
    }

    @PostMapping("/edit/{id}")
    public String updateTask(@ModelAttribute("task") Task task, @PathVariable("id") Long taskId, Model model, Authentication authentication) {
        CheckAuthorization(taskService.getTaskByID(taskId), authentication);
        Task updatedTask = taskService.updateTask(taskId, task);
        projectService.UpdateDates(updatedTask);
        model.addAttribute("project", updatedTask.getProject());
        return "edit_project";
    }

    private void CheckAuthorization(Task task, Authentication authentication) {
        AppUserPrincipal userPrincipal = (AppUserPrincipal)authentication.getPrincipal();
        if (userPrincipal.getUser().getUserId() != task.getProject().getUser().getUserId()) {
            throw new AppException("You have no access to this task");
        }
    }

    private void CheckProjectAuthorization(Project project, Authentication authentication) {
        AppUserPrincipal userPrincipal = (AppUserPrincipal)authentication.getPrincipal();
        if (userPrincipal.getUser().getUserId() != project.getUser().getUserId()) {
            throw new AppException("You have no access to this project");
        }
    }
}
