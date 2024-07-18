package com.giovaniwahl.dscatalog.controllers;

import com.giovaniwahl.dscatalog.dtos.NewPasswordDTO;
import com.giovaniwahl.dscatalog.dtos.RecoverTokenDTO;
import com.giovaniwahl.dscatalog.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/recover-token")
    public ResponseEntity<Void> createdRecoverToken(@Valid @RequestBody RecoverTokenDTO dto) {
        authService.createdRecoverToken(dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PutMapping("/new-password")
    public ResponseEntity<Void> saveNewPassword(@Valid @RequestBody NewPasswordDTO dto){
        authService.saveNewPassword(dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
