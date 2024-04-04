package com.giovaniwahl.dscatalog.services;
import com.giovaniwahl.dscatalog.dtos.CategoryDTO;
import com.giovaniwahl.dscatalog.entities.Category;
import com.giovaniwahl.dscatalog.repositories.CategoryRepository;
import com.giovaniwahl.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll(){
        List<Category> result = repository.findAll();
        return result.stream().map(CategoryDTO::new).toList();
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


    private void copyDtoToEntity(CategoryDTO dto, Category category) {
        category.setName(dto.getName());
    }
}
