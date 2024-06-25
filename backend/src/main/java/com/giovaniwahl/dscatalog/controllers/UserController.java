package com.giovaniwahl.dscatalog.controllers;

import com.giovaniwahl.dscatalog.dtos.UserDTO;
import com.giovaniwahl.dscatalog.dtos.UserDTO;
import com.giovaniwahl.dscatalog.dtos.UserInsertDTO;
import com.giovaniwahl.dscatalog.dtos.UserUpdateDTO;
import com.giovaniwahl.dscatalog.services.UserService;
import com.giovaniwahl.dscatalog.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService service;
    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(
            @PageableDefault(page = 0, size = 10, sort = "firstName", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll(pageable));
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(service.findById(id));
    }
    @PostMapping
    public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserInsertDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.insert(dto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id,@Valid @RequestBody UserUpdateDTO dto){
        return ResponseEntity.status(HttpStatus.OK).body(service.update(id, dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
