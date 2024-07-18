package com.giovaniwahl.dscatalog.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NewPasswordDTO {
    @NotBlank(message = "Required field.")
    private String token;
    @NotBlank(message = "Required field.")
    @Size(min = 8, message = "Must contain 8 characters or more.")
    private String password;

    public NewPasswordDTO() {}

    public NewPasswordDTO(String token, String password) {
        this.token = token;
        this.password = password;
    }

    public String getToken() {return token;}
    public void setToken(String token) {this.token = token;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
}
