package com.giovaniwahl.dscatalog.dtos;

import com.giovaniwahl.dscatalog.services.validation.UserInsertValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@UserInsertValid
public class UserInsertDTO extends UserDTO{
    @NotBlank(message = "Required field.")
    @Size(min = 8, message = "Must contain 8 characters or more.")
    private String password;

    public UserInsertDTO() {
        super();
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
