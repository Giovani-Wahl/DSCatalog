package com.giovaniwahl.dscatalog.services;

import com.giovaniwahl.dscatalog.dtos.RecoverTokenDTO;
import com.giovaniwahl.dscatalog.entities.PasswordRecover;
import com.giovaniwahl.dscatalog.entities.User;
import com.giovaniwahl.dscatalog.repositories.PasswordRecoverRepository;
import com.giovaniwahl.dscatalog.repositories.UserRepository;
import com.giovaniwahl.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {
    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;
    @Value("${email.password-recover.uri}")
    private String recoverUri;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordRecoverRepository recoverRepository;
    @Autowired
    private EmailService emailService;

    @Transactional
    public void createdRecoverToken(RecoverTokenDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail());
        if (user == null){
            throw new ResourceNotFoundException("E-mail not found");
        }
        String token = UUID.randomUUID().toString();
        PasswordRecover passwordRecover = new PasswordRecover();
        passwordRecover.setEmail(dto.getEmail());
        passwordRecover.setToken(token);
        passwordRecover.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));
        recoverRepository.save(passwordRecover);

        String body = "Acesse o link para definir uma nova senha (válido por " +tokenMinutes + "minutos):\n\n"
                + recoverUri + token;
        emailService.sendEmail(dto.getEmail(),"Recuperação de Senha",body);
    }
}
