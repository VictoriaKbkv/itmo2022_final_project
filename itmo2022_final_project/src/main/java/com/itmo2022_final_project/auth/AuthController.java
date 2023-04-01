package com.itmo2022_final_project.auth;

import com.itmo2022_final_project.security.PasswordConfig;
import com.itmo2022_final_project.user.User;
import com.itmo2022_final_project.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
    private UserService userService;
    private PasswordConfig passwordConfig;


    @Autowired
    public AuthController(UserService userService, PasswordConfig passwordConfig) {
        this.userService = userService;
        this.passwordConfig = passwordConfig;
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model){
        User user = new User();
        model.addAttribute("user", user);
        return "signup";
    }

    @PostMapping("/signup")
    public String saveNewUser(@ModelAttribute("user") User user, HttpServletRequest request){
        User newUser = userService.saveNewUser(user);
        return "redirect:/login";
    }

    @GetMapping("/projects")
    public String showProjects(Authentication authentication, Model model){
        model.addAttribute("projects", userService.getProjectsByUserName(authentication.getName()));
        return "projects";
    }

}
