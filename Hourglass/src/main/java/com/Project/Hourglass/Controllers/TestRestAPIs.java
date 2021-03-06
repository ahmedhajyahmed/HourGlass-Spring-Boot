package com.Project.Hourglass.Controllers;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class TestRestAPIs {



    @GetMapping("/api/test/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String userAccess() {
        return ">>> User Contents!";
    }


    @GetMapping("/api/test/coach")
    @PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
    public String coachAccess() {
        return ">>> Coach Board";
    }


    @GetMapping("/api/test/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return ">>> Admin Contents";
    }
}
