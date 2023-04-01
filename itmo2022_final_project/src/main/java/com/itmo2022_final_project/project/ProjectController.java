package com.itmo2022_final_project.project;

import com.itmo2022_final_project.exceptions.AppException;
import com.itmo2022_final_project.task.Task;
import com.itmo2022_final_project.project.Project;
import com.itmo2022_final_project.user.AppUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping(path = "project")
public class ProjectController {
    private final ProjectService projectService;
    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/new")
    public String showNewProjectForm(Model model) {
        Project project = new Project();
        model.addAttribute("project", project);
        return "add_project";
    }

    @PostMapping("/add-project")
    public String saveNewProject(@ModelAttribute("project") Project project, Authentication authentication) {
        AppUserPrincipal userPrincipal = (AppUserPrincipal)authentication.getPrincipal();
        project.setUser(userPrincipal.getUser());
        projectService.saveNewProject(project);
        return "edit_project";
    }

    @GetMapping("/edit/{id}")
    public String editProjectForm(@PathVariable Long id, Model model, Authentication authentication) {
        Project project = projectService.getProjectByID(id);
        CheckAuthorization(project, authentication);
        model.addAttribute("project", project);
        return "edit_project";
    }

    @GetMapping("/{id}/task/add")
    public String addProjectTaskForm(@PathVariable Long id, Model model, Authentication authentication) {
        Project project = projectService.getProjectByID(id);
        CheckAuthorization(project, authentication);
        Task task = new Task();
        task.setProject(project);
        model.addAttribute("task", task);
        return "add_task";
    }

    @GetMapping("/update/{id}")
    public String updateProject(@PathVariable Long id, Model model, Authentication authentication) {
        Project project = projectService.getProjectByID(id);
        CheckAuthorization(project, authentication);
        model.addAttribute("project", projectService.UpdateProject(id));
        return "edit_project";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable("id") Long id, Authentication authentication){
        CheckAuthorization(projectService.getProjectByID(id), authentication);
        projectService.deleteProject(id);
        return "redirect:/projects";
    }

    private void CheckAuthorization(Project project, Authentication authentication) {
        AppUserPrincipal userPrincipal = (AppUserPrincipal)authentication.getPrincipal();
        if (userPrincipal.getUser().getUserId() != project.getUser().getUserId()) {
            throw new AppException("You have no access to this project");
        }
    }

}
