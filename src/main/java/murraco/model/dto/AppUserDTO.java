package murraco.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class AppUserDTO {
    private Integer id;
    private String username;
    private String email;
    private String password;
    private List<String> appUserRoles;
}
