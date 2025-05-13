package com.kc.integration.service;

import com.kc.integration.model.UserDetails;
import com.kc.integration.repository.UserDetailsRepo;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeyCloakService {

    private final Keycloak keycloak;
    private final String realm;
    private final UserDetailsRepo userDetailsRepo;

    public KeyCloakService(Keycloak keycloak, String realm, UserDetailsRepo userDetailsRepo) {
        this.keycloak = keycloak;
        this.realm = realm;
        this.userDetailsRepo = userDetailsRepo;
    }

    public List<UserDetails> getUserNameList(){
        return userDetailsRepo.findAll();
//        return userDetailsList.stream().map(UserDetails::getFirstnameEn).collect(Collectors.toList());
    }

    public void createUser(String userName, String email, String password){
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userName);
        user.setEmail(email);
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        user.setCredentials(Collections.singletonList(credential));
        keycloak.realm(realm).users().create(user);

    }

    public void updateUser(String userId, String newEmail){
        UserResource userResource = keycloak.realm(realm).users().get(userId);
        UserRepresentation user = userResource.toRepresentation();
        user.setEmail(newEmail);
        userResource.update(user);
    }

    public void deleteUser(String userId) {
        keycloak.realm(realm).users().get(userId).remove();
    }

    public List<String> getUserRoles(String userId) {
        return keycloak.realm(realm)
                .users().get(userId)
                .roles().realmLevel()
                .listEffective()
                .stream().map(RoleRepresentation::getName)
                .collect(Collectors.toList());
    }

    public String getUserIdByUsername(String username) {
        List<UserRepresentation> users = keycloak.realm(realm).users().search(username);
        if (users.isEmpty()) throw new RuntimeException("User not found");
        return users.get(0).getId();
    }

    public void assignRoleToUser(String realm, String userId, String roleName) {
        RoleRepresentation role = keycloak.realm(realm)
                .roles().get(roleName).toRepresentation();

        keycloak.realm(realm)
                .users().get(userId)
                .roles().realmLevel()
                .add(Collections.singletonList(role));
    }

    public void updateUserRoles(String realm, String userId, List<String> newRoleNames) {
        List<RoleRepresentation> currentRoles = keycloak.realm(realm)
                .users().get(userId).roles().realmLevel().listAll();

        keycloak.realm(realm)
                .users().get(userId).roles().realmLevel().remove(currentRoles);

        List<RoleRepresentation> newRoles = newRoleNames.stream()
                .map(roleName -> keycloak.realm(realm).roles().get(roleName).toRepresentation())
                .collect(Collectors.toList());

        keycloak.realm(realm)
                .users().get(userId).roles().realmLevel().add(newRoles);
    }

}
