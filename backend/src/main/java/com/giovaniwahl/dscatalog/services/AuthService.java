package com.giovaniwahl.dscatalog.services;

import com.giovaniwahl.dscatalog.dtos.NewPasswordDTO;
import com.giovaniwahl.dscatalog.dtos.RecoverTokenDTO;
import com.giovaniwahl.dscatalog.entities.PasswordRecover;
import com.giovaniwahl.dscatalog.entities.User;
import com.giovaniwahl.dscatalog.repositories.PasswordRecoverRepository;
import com.giovaniwahl.dscatalog.repositories.UserRepository;
import com.giovaniwahl.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {
    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;
    @Value("${email.password-recover.uri}")
    private String recoverUri;

    @Autowired
    private PasswordEncoder passwordEncoder;
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

    @Transactional
    public void saveNewPassword(NewPasswordDTO dto) {
        List<PasswordRecover> result = recoverRepository.searchValidTokens(dto.getToken(), Instant.now());
        if (result.isEmpty()){
            throw new ResourceNotFoundException("Invalid token");
        }
        User user = userRepository.findByEmail(result.get(0).getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
    }
    protected User authenticated(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");
            return userRepository.findByEmail(username);
        }
        catch (Exception e){
            throw new UsernameNotFoundException("Invalid user");
        }
    }
}
