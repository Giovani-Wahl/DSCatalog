package com.giovaniwahl.dscatalog.services;
import com.giovaniwahl.dscatalog.dtos.CategoryDTO;
import com.giovaniwahl.dscatalog.dtos.ProductDTO;
import com.giovaniwahl.dscatalog.entities.Category;
import com.giovaniwahl.dscatalog.entities.Product;
import com.giovaniwahl.dscatalog.projections.ProductProjection;
import com.giovaniwahl.dscatalog.repositories.CategoryRepository;
import com.giovaniwahl.dscatalog.repositories.ProductRepository;
import com.giovaniwahl.dscatalog.services.exceptions.DatabaseException;
import com.giovaniwahl.dscatalog.services.exceptions.ResourceNotFoundException;
import com.giovaniwahl.dscatalog.util.Utils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;
    @Autowired
    private CategoryRepository categoryRepository;


    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(String categoryId, String name, Pageable pageable) {
        List<Long> categoryIds = new ArrayList<>();
        if (!"0".equals(categoryId)) {
            categoryIds = Arrays.stream(categoryId.split(",")).map(Long::parseLong).toList();
        }
        Page<ProductProjection> page = repository.searchProducts(categoryIds,name,pageable);
        List<Long> productIds = page.map(ProductProjection::getId).toList();
        List<Product> entities = repository.searchProductsWithCategories(productIds);
        entities = (List<Product>) Utils.replace(page.getContent(),entities);
        List<ProductDTO> dtos = entities.stream().map(ProductDTO::new).toList();
        return new PageImpl<>(dtos,page.getPageable(), page.getTotalElements());
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
