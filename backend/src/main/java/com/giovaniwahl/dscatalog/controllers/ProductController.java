package com.giovaniwahl.dscatalog.controllers;

import com.giovaniwahl.dscatalog.dtos.ProductDTO;
import com.giovaniwahl.dscatalog.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService service;
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> findAll(
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll(pageable));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(service.findById(id));
    }
    @PostMapping
    public ResponseEntity<ProductDTO> insert(@RequestBody ProductDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.insert(dto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id,@RequestBody ProductDTO dto){
        return ResponseEntity.status(HttpStatus.OK).body(service.update(id, dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}