package com.womensecurity.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.entity.User;
import com.womensecurity.backend.service.UserService;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin("*")
public class AdminUserController {

    @Autowired
    private UserService userService;

    // ==========================================
    // GET ALL USERS
    // ==========================================

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // ==========================================
    // GET USER BY ID
    // ==========================================

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // ==========================================
    // UPDATE USER
    // ==========================================

    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser) {

        return userService.updateUser(id, updatedUser);
    }
  @PutMapping("/{id}/block")
public ResponseEntity<?> blockUser(@PathVariable Long id) {

    User user = userService.getUserById(id);

    if (user == null) {
        return ResponseEntity.notFound().build();
    }

    if ("SUPER_ADMIN".equalsIgnoreCase(user.getRole())) {
        return ResponseEntity
                .badRequest()
                .body("SUPER_ADMIN account cannot be blocked.");
    }

    return ResponseEntity.ok(
            userService.blockUser(id)
    );
}


@PutMapping("/{id}/unblock")
public ResponseEntity<?> unblockUser(
        @PathVariable Long id) {

    return ResponseEntity.ok(
            userService.unblockUser(id)
    );
}


@PutMapping("/{id}/ban")
public ResponseEntity<?> banUser(@PathVariable Long id) {

    User user = userService.getUserById(id);

    if (user == null) {
        return ResponseEntity.notFound().build();
    }

    if ("SUPER_ADMIN".equalsIgnoreCase(user.getRole())) {
        return ResponseEntity
                .badRequest()
                .body("SUPER_ADMIN account cannot be banned.");
    }

    return ResponseEntity.ok(
            userService.banUser(id)
    );
}


@DeleteMapping("/{id}")
public ResponseEntity<?> deleteUser(
        @PathVariable Long id) {

    User user = userService.getUserById(id);

    if (user == null) {
        return ResponseEntity.notFound().build();
    }

    if ("SUPER_ADMIN".equalsIgnoreCase(user.getRole())) {
        return ResponseEntity
                .badRequest()
                .body("SUPER_ADMIN account cannot be deleted.");
    }

    return ResponseEntity.ok(
            userService.deleteUser(id)
    );
}
}