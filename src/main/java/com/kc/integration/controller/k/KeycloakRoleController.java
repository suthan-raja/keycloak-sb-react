package com.kc.integration.controller.k;

import com.kc.integration.config.KeycloakAdminConfig;
import com.kc.integration.controller.KeyCloakController;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/keycloak/roles")
@Slf4j
public class KeycloakRoleController {

    private KeycloakAdminConfig config;

    @Autowired
    public KeycloakRoleController(KeycloakAdminConfig config) {
        this.config = config;
    }

    @PostMapping
    public ResponseEntity<String> createRole(@RequestBody RoleRepresentation role) {
        Keycloak keycloak = config.InitiateKeyCloak();
        String realmName = config.getRealm();
        log.info("Realm Name : {}", realmName);
        try {
            List<RoleRepresentation> existingRoles = keycloak.realm(realmName).roles().list();
            boolean roleExists = existingRoles.stream().anyMatch(r -> r.getName().equals(role.getName()));
            if (roleExists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Role already exists: " + role.getName());
            } else {
                keycloak.realm(realmName).roles().create(role);
                return ResponseEntity.ok("Role created: " + role.getName());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while creating role: " + e.getMessage());
        }
    }

    @GetMapping
    public List<RoleRepresentation> getRoles() {
        Keycloak keycloak = config.InitiateKeyCloak();
        return keycloak.realm(config.getRealm()).roles().list();
    }

    @PostMapping("/{userId}/assign")
    public ResponseEntity<String> assignRole(@PathVariable String userId, @RequestParam String roleName) {
        Keycloak keycloak = config.InitiateKeyCloak();
        RoleRepresentation role = keycloak.realm(config.getRealm()).roles().get(roleName).toRepresentation();
        keycloak.realm(config.getRealm()).users().get(userId).roles().realmLevel().add(List.of(role));
        return ResponseEntity.ok("Role assigned to user");
    }

    @PostMapping("/{userId}/assign-client-role")
    public ResponseEntity<String> assignRoleClientRole(@PathVariable String userId,
                                                       @RequestParam String clientId,
                                                       @RequestParam String roleName) {
        Keycloak keycloak = config.InitiateKeyCloak();
        String realmName = config.getRealm();

        String clientUUID = keycloak.realm(realmName).clients().findByClientId(clientId).get(0).getId();

        RoleRepresentation clientRole = keycloak.realm(realmName)
                .clients().get(clientUUID)
                .roles().get(roleName)
                .toRepresentation();

        keycloak.realm(realmName).users().get(userId).roles().clientLevel(clientUUID).add(List.of(clientRole));

        return ResponseEntity.ok("Role assigned client role");

    }

    @GetMapping("/client-roles/{userId}")
    public ResponseEntity<List<String>> getClientRoles(@PathVariable String userId,
                                                       @RequestParam String clientId) {
        Keycloak keycloak = config.InitiateKeyCloak();
        String realm = config.getRealm();

        String clientUUID = keycloak.realm(realm).clients().findByClientId(clientId).get(0).getId();

        List<RoleRepresentation> roles = keycloak.realm(realm).users()
                .get(userId).roles().clientLevel(clientUUID).listAll();

        List<String> roleNames = roles.stream().map(RoleRepresentation::getName).collect(Collectors.toList());

        return ResponseEntity.ok(roleNames);
    }


    @PostMapping("/{userId}/remove")
    public ResponseEntity<String> removeRole(@PathVariable String userId, @RequestParam String roleName) {
        Keycloak keycloak = config.InitiateKeyCloak();
        RoleRepresentation role = keycloak.realm(config.getRealm()).roles().get(roleName).toRepresentation();
        keycloak.realm(config.getRealm()).users().get(userId).roles().realmLevel().remove(List.of(role));
        return ResponseEntity.ok("Role removed from user");
    }

    /*@GetMapping("/{userId}")
    public List<RoleRepresentation> getUserRoles(@PathVariable String userId) {
        Keycloak keycloak = config.keyCloakAdminClient();
        return keycloak.realm(config.keycloakRealm()).users().get(userId).roles().realmLevel().listEffective();
    }*/

    @GetMapping("/get-roles/{userId}")
    public ResponseEntity<List<String>> getRolesByUserId(@PathVariable String userId) {
        Keycloak keycloak = config.InitiateKeyCloak();
        RealmResource realm = keycloak.realm(config.getRealm());

        // Get the list of roles assigned to the user
        List<RoleRepresentation> roles = realm.users().get(userId).roles().realmLevel().listAll();

        List<String> roleNames = roles.stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());

        return ResponseEntity.ok(roleNames);
    }

    @GetMapping("/get-user-roles/{userId}")
    public ResponseEntity<List<String>> getUserRoles(@PathVariable String userId) {
        Keycloak keycloak = config.InitiateKeyCloak();
        RealmResource realm = keycloak.realm(config.getRealm());

        // Get user roles from the realm
        List<RoleRepresentation> roles = realm.users().get(userId).roles().realmLevel().listAll();

        List<String> roleNames = roles.stream().map(RoleRepresentation::getName).collect(Collectors.toList());

        return ResponseEntity.ok(roleNames);
    }

    @PostMapping("/create-client-role")
    public ResponseEntity<String> createClientRole(@RequestParam String clientId,
                                                   @RequestParam String roleName) {
        try {
            Keycloak keycloak = config.InitiateKeyCloak();
            String realmName = config.getRealm();

            String clientUUId = keycloak.realm(realmName).clients().findByClientId(clientId).get(0).getId();

            List<RoleRepresentation> clientRoles = keycloak.realm(realmName).clients().get(clientUUId).roles().list();

            boolean exists = clientRoles.stream().anyMatch(role -> role.getName().equals(roleName));

            if (exists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Client role already exists: " + roleName);
            }

            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            keycloak.realm(realmName).clients().get(clientUUId).roles().create(role);

            return ResponseEntity.ok("Client role created: " + roleName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating client role: " + e.getMessage());
        }
    }

    @PostMapping("/client-roles-list")
    public ResponseEntity<List<String>> getCreatedClientRoles(@RequestParam String clientId) {
        try {
            Keycloak keycloak = config.InitiateKeyCloak();
            String realmName = config.getRealm();

            String clientUUID = keycloak.realm(realmName)
                    .clients().findByClientId(clientId).get(0).getId();

            List<RoleRepresentation> roles = keycloak.realm(realmName)
                    .clients().get(clientUUID).roles().list();

            List<String> roleNames = roles.stream()
                    .map(RoleRepresentation::getName)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(roleNames);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of("Error: " + e.getMessage()));
        }
    }


    @PostMapping("/{userId}/remove-client-role")
    public ResponseEntity<String> removeClientRole(@PathVariable String userId,
                                                   @RequestParam String clientId,
                                                   @RequestParam String roleName) {
        try {
            Keycloak keycloak = config.InitiateKeyCloak();
            String realmName = config.getRealm();

            String clientUUID = keycloak.realm(realmName)
                    .clients().findByClientId(clientId).get(0).getId();

            RoleRepresentation role = keycloak.realm(realmName)
                    .clients().get(clientUUID)
                    .roles().get(roleName)
                    .toRepresentation();

            keycloak.realm(realmName).users()
                    .get(userId).roles()
                    .clientLevel(clientUUID).remove(List.of(role));

            return ResponseEntity.ok("Client role removed from user");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error removing client role: " + e.getMessage());
        }
    }


}
