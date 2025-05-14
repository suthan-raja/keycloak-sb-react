package com.kc.integration.controller;

import com.kc.integration.dto.KeycloakTokenResponse;
import com.kc.integration.dto.LoginRequest;
import com.kc.integration.model.UserDetails;
import com.kc.integration.service.KeyCloakService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class KeyCloakController {

    @Value("${keycloak.auth-server-url}")
    private String keycloakBaseUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private final KeyCloakService keyCloakService;

    public KeyCloakController(KeyCloakService keyCloakService) {
        this.keyCloakService = keyCloakService;
    }

    @PostMapping("/login")
    public ResponseEntity<KeycloakTokenResponse> login(@RequestBody LoginRequest request) {

        System.out.println(request);

        String tokenUrl = keycloakBaseUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = getStringHttpEntity(request, headers);

        ResponseEntity<KeycloakTokenResponse> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                entity,
                KeycloakTokenResponse.class
        );

        return ResponseEntity.ok(response.getBody());
    }

    @PreAuthorize("hasRole('user-manager')")
    @GetMapping("get-user-db")
    public List<String> getUsers(){
        return keyCloakService.getUserNameList().stream().map(UserDetails::getFirstnameEn).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("admin")
    public String getAdminAccess(){
        return "I am admin";
    }

    private HttpEntity<String> getStringHttpEntity(LoginRequest request, HttpHeaders headers) {
        Map<String, String> bodyMap = new LinkedHashMap<>();
        bodyMap.put("grant_type", "password");
        bodyMap.put("client_id", clientId);
        bodyMap.put("client_secret", clientSecret);
        bodyMap.put("username", request.getUsername());
        bodyMap.put("password", request.getPassword());

        StringBuilder bodyBuilder = new StringBuilder();
        bodyMap.forEach((key, value) -> bodyBuilder.append(key).append("=").append(value).append("&"));
        String body = bodyBuilder.substring(0, bodyBuilder.length() - 1);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        return entity;
    }
}
