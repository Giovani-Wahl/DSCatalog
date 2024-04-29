package com.giovaniwahl.dscatalog.services;

import com.giovaniwahl.dscatalog.dtos.ProductDTO;
import com.giovaniwahl.dscatalog.repositories.ProductRepository;
import com.giovaniwahl.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceIntTests {

    @Autowired
    private ProductService service;
    @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private Long countTotalProducts;

    @BeforeEach
    void setup()throws Exception{
        existingId=1L;
        nonExistingId=2000L;
        dependentId=3L;
        countTotalProducts=25L;
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists(){
        service.delete(existingId);
        Assertions.assertEquals(countTotalProducts-1,repository.count());
    }
    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists(){
        Assertions.assertThrows(ResourceNotFoundException.class,()->{
            service.delete(nonExistingId);
        });
    }
    @Test
    public void findAllPageShouldReturnPageWhenPage0(){
        PageRequest pageRequest = PageRequest.of(0,10);
        Page<ProductDTO> result = service.findAll(pageRequest);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0,result.getNumber());
        Assertions.assertEquals(10,result.getSize());
        Assertions.assertEquals(countTotalProducts,result.getTotalElements());
    }
    @Test
    public void findAllPageShouldReturnEmptyPageWhenPageDoesNotExists(){
        PageRequest pageRequest = PageRequest.of(5000,10);
        Page<ProductDTO> result = service.findAll(pageRequest);
        Assertions.assertTrue(result.isEmpty());
    }
    @Test
    public void findAllPageShouldReturnSortedPageWhenSortByName(){
        PageRequest pageRequest = PageRequest.of(0,10, Sort.by("name"));
        Page<ProductDTO> result = service.findAll(pageRequest);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Macbook Pro",result.getContent().get(0).getName());
    }
}
