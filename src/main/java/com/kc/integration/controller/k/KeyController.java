package com.kc.integration.controller.k;

import com.kc.integration.config.KeycloakAdminConfig;
import com.kc.integration.dto.UserRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
//import org.keycloak.representations.account.UserRepresentation;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/keycloak/users")
//@Slf4j
public class KeyController {

    @Autowired
    private KeycloakAdminConfig config;

    /*@PostMapping("/create-user")
    public ResponseEntity<String> createUser(@RequestBody UserRequestDTO request) {
        Keycloak keycloak = config.keyCloakAdminClient();
        RealmResource realm = keycloak.realm(config.keycloakRealm());

        // 1. Create basic user
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);

        Response response = realm.users().create(user);

        if (response.getStatus() != 201) {
            return ResponseEntity.status(response.getStatus()).body("Failed to create user");
        }

        // 2. Get created user ID
        String userId = CreatedResponseUtil.getCreatedId(response);

        // 3. Set password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);

        realm.users().get(userId).resetPassword(credential);

        // 4. Assign roles
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            List<RoleRepresentation> realmRoles = realm.roles().list().stream()
                    .filter(role -> request.getRoles().contains(role.getName()))
                    .collect(Collectors.toList());

            realm.users().get(userId).roles().realmLevel().add(realmRoles);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully with roles");
    }*/

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody UserRequestDTO request) {
        Keycloak keycloak = config.keyCloakAdminClient();
        RealmResource realmResource = keycloak.realm(config.keycloakRealm());

        // Step 1: Create the user
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setEnabled(true);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        // Create user
        Response response = realmResource.users().create(user);

        if (response.getStatus() != 201) {
            return ResponseEntity.status(response.getStatus()).body("User creation failed");
        }

        // Step 2: Get the userId from response
        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        // Step 3: Set password
        CredentialRepresentation password = new CredentialRepresentation();
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue(request.getPassword());
        password.setTemporary(false);

        realmResource.users().get(userId).resetPassword(password);

        System.out.println("Roles : {}"+request.getRoles());

        // Step 4: Assign roles (if any)
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            List<RoleRepresentation> rolesToAssign = request.getRoles().stream()
                    .map(roleName -> realmResource.roles().get(roleName).toRepresentation())
                    .collect(Collectors.toList());

            realmResource.users().get(userId).roles().realmLevel().add(rolesToAssign);
        }

        return ResponseEntity.status(201).body("User created and roles assigned");
    }




    @GetMapping
    public List<UserRepresentation> getAllUsers() {
        Keycloak keycloak = config.keyCloakAdminClient();
        return keycloak.realm(config.keycloakRealm()).users().list();
    }

    @GetMapping("/{userId}")
    public UserRepresentation getUser(@PathVariable String userId) {
        Keycloak keycloak = config.keyCloakAdminClient();
        return keycloak.realm(config.keycloakRealm()).users().get(userId).toRepresentation();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable String userId, @RequestBody UserRepresentation user) {
        Keycloak keycloak = config.keyCloakAdminClient();
        keycloak.realm(config.keycloakRealm()).users().get(userId).update(user);
        return ResponseEntity.ok("User updated");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        Keycloak keycloak = config.keyCloakAdminClient();
        keycloak.realm(config.keycloakRealm()).users().get(userId).remove();
        return ResponseEntity.ok("User deleted");
    }
}
