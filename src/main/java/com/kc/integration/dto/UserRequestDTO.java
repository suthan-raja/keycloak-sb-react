package com.kc.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private List<String> roles;
}
