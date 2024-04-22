package com.giovaniwahl.dscatalog.controllers;

import com.giovaniwahl.dscatalog.Factory;
import com.giovaniwahl.dscatalog.dtos.ProductDTO;
import com.giovaniwahl.dscatalog.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    private ProductDTO dto;
    private PageImpl<ProductDTO> page;

    @BeforeEach
    void setUp()throws Exception{
        dto = Factory.createProductDTO();
        page = new PageImpl<>(List.of(dto));
        when(service.findAll(any())).thenReturn(page);
    }

    @Test
    public void findAllShouldReturnPage()throws Exception{
        mockMvc.perform(get("/products")).andExpect(status().isOk());
    }
}
