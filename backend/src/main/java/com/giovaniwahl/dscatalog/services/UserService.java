package com.giovaniwahl.dscatalog.services;
import com.giovaniwahl.dscatalog.dtos.RoleDTO;
import com.giovaniwahl.dscatalog.dtos.UserDTO;
import com.giovaniwahl.dscatalog.dtos.UserInsertDTO;
import com.giovaniwahl.dscatalog.dtos.UserUpdateDTO;
import com.giovaniwahl.dscatalog.entities.Role;
import com.giovaniwahl.dscatalog.entities.User;
import com.giovaniwahl.dscatalog.projections.UserDetailsProjection;
import com.giovaniwahl.dscatalog.repositories.RoleRepository;
import com.giovaniwahl.dscatalog.repositories.UserRepository;
import com.giovaniwahl.dscatalog.services.exceptions.DatabaseException;
import com.giovaniwahl.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository repository;
    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable){
        Page<User> result = repository.findAll(pageable);
        return result.map(UserDTO::new);
    }
    @Transactional(readOnly = true)
    public UserDTO findById(Long id){
       User user = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Id Not Found."));
       return new UserDTO(user);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto){
        try {
            User user = new User();
            copyDtoToEntity(dto,user);
            user.getRoles().clear();
            Role role = roleRepository.findByAuthority("ROLE_OPERATOR");
            user.getRoles().add(role);
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user = repository.save(user);
            return new UserDTO(user);
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Role does not exist!");
        }
    }
    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto){
        try {
            User user = repository.getReferenceById(id);
            copyDtoToEntity(dto,user);
            user = repository.save(user);
            return new UserDTO(user);
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Resource not found!");
        }
    }
    @Transactional
    public void delete(Long id){
        if (!repository.existsById(id)){
            throw new ResourceNotFoundException("Resource not found !");
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e){
            throw new DatabaseException("Referential integrity failure !");
        }
    }

    private void copyDtoToEntity(UserDTO dto, User user) {
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.getRoles().clear();
        for (RoleDTO roleDTO: dto.getRoles()){
            Role role = roleRepository.getReferenceById(roleDTO.getId());
            user.getRoles().add(role);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
        if (result.isEmpty()){
            throw new UsernameNotFoundException("User Name Not Found.");
        }
        User user = new User();
        user.setEmail(result.get(0).getUsername());
        user.setPassword(result.get(0).getPassword());
        for (UserDetailsProjection projection : result){
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }
        return user;
    }
}
