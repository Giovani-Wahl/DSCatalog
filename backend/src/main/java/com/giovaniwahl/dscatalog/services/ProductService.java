package com.giovaniwahl.dscatalog.services;
import com.giovaniwahl.dscatalog.dtos.CategoryDTO;
import com.giovaniwahl.dscatalog.dtos.ProductDTO;
import com.giovaniwahl.dscatalog.entities.Category;
import com.giovaniwahl.dscatalog.entities.Product;
import com.giovaniwahl.dscatalog.repositories.CategoryRepository;
import com.giovaniwahl.dscatalog.repositories.ProductRepository;
import com.giovaniwahl.dscatalog.services.exceptions.DatabaseException;
import com.giovaniwahl.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable){
        Page<Product> result = repository.findAll(pageable);
        return result.map(ProductDTO::new);
    }
    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){
       Product product = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Id Not Found."));
       return new ProductDTO(product);
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto){
        try {
            Product product = new Product();
            copyDtoToEntity(dto,product);
            product = repository.save(product);
            return new ProductDTO(product);
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Category does not exist!");
        }
    }
    @Transactional
    public ProductDTO update(Long id, ProductDTO dto){
        try {
            Product product = repository.getReferenceById(id);
            copyDtoToEntity(dto,product);
            product = repository.save(product);
            return new ProductDTO(product);
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

    private void copyDtoToEntity(ProductDTO dto, Product product) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImgUrl(dto.getImgUrl());
        product.setDate(dto.getDate());
        product.getCategories().clear();
        for (CategoryDTO catDTO : dto.getCategories()){
            Category cat = categoryRepository.getReferenceById(catDTO.getId());
            product.getCategories().add(cat);
        }
    }
}
