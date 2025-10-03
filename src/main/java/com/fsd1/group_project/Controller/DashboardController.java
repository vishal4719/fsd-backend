package com.fsd1.group_project.Controller;

import com.fsd1.group_project.Entity.Participation;
import com.fsd1.group_project.Entity.User;
import com.fsd1.group_project.Service.ParticipationService;
import com.fsd1.group_project.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private ParticipationService participationService;

    @Autowired
    private UserService userService;

    // REST endpoints for role-based access
    @GetMapping("/roles/user")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<?> viewerDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Check if user has submitted participation form
        boolean hasSubmittedForm = participationService.hasUserSubmittedForm(username);

        return ResponseEntity.ok(Map.of(
                "message", "Welcome to Viewer Dashboard",
                "username", username,
                "role", "USER",
                "hasSubmittedForm", hasSubmittedForm
        ));
    }

    @GetMapping("/roles/task-manager")
    @PreAuthorize("hasRole('TASK_MANAGER')")
    @ResponseBody
    public ResponseEntity<?> taskManagerDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return ResponseEntity.ok(Map.of(
                "message", "Welcome to Task Manager Dashboard",
                "username", username,
                "role", "TASK_MANAGER"
        ));
    }

    // New endpoints for participation form flow
    @GetMapping("/participation/form")
    @PreAuthorize("hasRole('USER')")
    public String showParticipationForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Check if user already submitted the form
        if (participationService.hasUserSubmittedForm(username)) {
            return "redirect:/api/dashboard";
        }

        model.addAttribute("participation", new Participation());
        return "participation-form";
    }
    @PostMapping("/participation/submit")
  @PreAuthorize("hasRole('USER')")
  @ResponseBody
  public ResponseEntity<?> submitParticipationForm(@RequestBody Participation participation) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    User user = userService.findByEmail(username)
      .orElseThrow(() -> new RuntimeException("User not found"));

    participationService.saveParticipation(participation, user);

    return ResponseEntity.ok(Map.of("message", "Participation submitted successfully"));
  }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('USER', 'TASK_MANAGER')")
    public String showDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Get current user using existing method
        User user = userService.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user has submitted participation form
        boolean hasSubmittedForm = participationService.hasUserSubmittedForm(username);

        model.addAttribute("user", user);
        model.addAttribute("hasSubmittedForm", hasSubmittedForm);

        // If form submitted, add participation data
        if (hasSubmittedForm) {
            Optional<Participation> participation = participationService.getParticipationByUser(username);
            participation.ifPresent(part -> model.addAttribute("participation", part));
        }

        return "dashboard";
    }

    // REST endpoint to check participation status
    @GetMapping("/participation/status")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<?> getParticipationStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        boolean hasSubmittedForm = participationService.hasUserSubmittedForm(username);

        return ResponseEntity.ok(Map.of(
                "hasSubmittedForm", hasSubmittedForm,
                "username", username
        ));
    }
}
