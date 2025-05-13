package com.kc.integration.controller;

import com.kc.integration.service.KeyCloakService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/keycloak/users")
public class KeycloakUserControllerOld {

    private final KeyCloakService keycloakUserService;

    public KeycloakUserControllerOld(KeyCloakService keycloakUserService) {
        this.keycloakUserService = keycloakUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String username,
                                           @RequestParam String email,
                                           @RequestParam String password) {
        keycloakUserService.createUser(username, email, password);
        return ResponseEntity.ok("User registered in Keycloak");
    }

    @PutMapping("/{username}/update-email")
    public ResponseEntity<String> updateEmail(@PathVariable String username,
                                              @RequestParam String newEmail) {
        String userId = keycloakUserService.getUserIdByUsername(username);
        keycloakUserService.updateUser(userId, newEmail);
        return ResponseEntity.ok("Email updated");
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        String userId = keycloakUserService.getUserIdByUsername(username);
        keycloakUserService.deleteUser(userId);
        return ResponseEntity.ok("User deleted from Keycloak");
    }

    @GetMapping("/{username}/roles")
    public ResponseEntity<List<String>> getRoles(@PathVariable String username) {
        String userId = keycloakUserService.getUserIdByUsername(username);
        List<String> roles = keycloakUserService.getUserRoles(userId);
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/assign-role")
    public ResponseEntity<String> assignRole(
            @RequestParam String userId,
            @RequestParam String roleName
    ) {
        keycloakUserService.assignRoleToUser("your-realm", userId, roleName);
        return ResponseEntity.ok("Role assigned");
    }


}
