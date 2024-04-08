package com.giovaniwahl.dscatalog.services;
import com.giovaniwahl.dscatalog.dtos.CategoryDTO;
import com.giovaniwahl.dscatalog.entities.Category;
import com.giovaniwahl.dscatalog.repositories.CategoryRepository;
import com.giovaniwahl.dscatalog.services.exceptions.DatabaseException;
import com.giovaniwahl.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(Pageable pageable){
        Page<Category> result = repository.findAll(pageable);
        return result.map(CategoryDTO::new);
    }
    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
       Category category = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Id Not Found."));
       return new CategoryDTO(category);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto){
        Category category = new Category();
        category.setName(dto.getName());
       category = repository.save(category);
        return new CategoryDTO(category);
    }
    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto){
        try {
            Category category = repository.getReferenceById(id);
            copyDtoToEntity(dto,category);
            category = repository.save(category);
            return new CategoryDTO(category);
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


    private void copyDtoToEntity(CategoryDTO dto, Category category) {
        category.setName(dto.getName());
    }
}
