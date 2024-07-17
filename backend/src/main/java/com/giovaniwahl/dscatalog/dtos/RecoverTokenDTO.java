package com.giovaniwahl.dscatalog.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RecoverTokenDTO {
    @JsonProperty(value = "email")
    @NotBlank(message = "Required field.")
    @Email(message = "Email is not valid.")
    private String email;

    public RecoverTokenDTO() {}
    public RecoverTokenDTO(String email) {this.email = email;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
}
