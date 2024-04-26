package com.giovaniwahl.dscatalog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.giovaniwahl.dscatalog.Factory;
import com.giovaniwahl.dscatalog.dtos.ProductDTO;
import com.giovaniwahl.dscatalog.services.ProductService;
import com.giovaniwahl.dscatalog.services.exceptions.DatabaseException;
import com.giovaniwahl.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO dto;
    private PageImpl<ProductDTO> page;
    private Long existingId;
    private Long nonExistingId;
    private long dependentId;
    private String jsonBody;

    @BeforeEach
    void setUp()throws Exception{
        existingId = 1L;
        nonExistingId=2L;
        dependentId = 3L;
        dto = Factory.createProductDTO();
        page = new PageImpl<>(List.of(dto));
        jsonBody = objectMapper.writeValueAsString(dto);

        when(service.findAll(any())).thenReturn(page);

        when(service.findById(existingId)).thenReturn(dto);
        when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        when(service.update(eq(existingId),any())).thenReturn(dto);
        when(service.update(eq(nonExistingId),any())).thenThrow(ResourceNotFoundException.class);

        when(service.insert(any())).thenReturn(dto);

        doNothing().when(service).delete(existingId);
        doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
        doThrow(DatabaseException.class).when(service).delete(dependentId);
    }

    @Test
    public void findAllShouldReturnPage()throws Exception{
        mockMvc.perform(get("/products")).andExpect(status().isOk());
    }
    @Test
    public void findByIdShouldReturnProductWhenIdExists()throws Exception{
        mockMvc.perform(get("/products/{id}",existingId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist()throws Exception{
        mockMvc.perform(get("/products/{id}",nonExistingId))
                .andExpect(status().isNotFound());
    }
    @Test
    public void updateShouldReturnDtoWhenIdExists()throws Exception{
                mockMvc.perform(put("/products/{id}",existingId)
                .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }
    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExists()throws Exception{
                mockMvc.perform(put("/products/{id}",nonExistingId)
                .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    public void insertShouldReturnDtoAndStatusCreatedWhenRegisterOk()throws Exception{
                mockMvc.perform(post("/products",dto)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }
    @Test
    public void deleteShouldDoesNotReturnWhenIdExists()throws Exception{
        mockMvc.perform(delete("/products/{id}",existingId))
                .andExpect(status().isNoContent());
    }
    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExists()throws Exception{
        mockMvc.perform(delete("/products/{id}",nonExistingId))
                .andExpect(status().isNotFound());
    }
}
