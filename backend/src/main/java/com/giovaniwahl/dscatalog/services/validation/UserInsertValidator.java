package com.giovaniwahl.dscatalog.services.validation;

import com.giovaniwahl.dscatalog.dtos.FieldMessage;
import com.giovaniwahl.dscatalog.dtos.UserInsertDTO;
import com.giovaniwahl.dscatalog.entities.User;
import com.giovaniwahl.dscatalog.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {
    private final UserRepository repository;

    public UserInsertValidator(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void initialize(UserInsertValid ann) {
    }

    @Override
    public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {
        List<FieldMessage> list = new ArrayList<>();
        User user = repository.findByEmail(dto.getEmail());
        if (user != null){
            list.add(new FieldMessage("email","E-mail already registered."));
        }
        for (FieldMessage e:list){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }
        return list.isEmpty();
    }
}
