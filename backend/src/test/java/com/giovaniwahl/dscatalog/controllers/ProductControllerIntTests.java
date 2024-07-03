package com.giovaniwahl.dscatalog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.giovaniwahl.dscatalog.Factory;
import com.giovaniwahl.dscatalog.TokenUtil;
import com.giovaniwahl.dscatalog.dtos.ProductDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIntTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TokenUtil tokenUtil;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private Long countTotalProducts;
    private ProductDTO dto;
    private String jsonBody;
    private String username, password, bearerToken;

    @BeforeEach
    void setup()throws Exception{
        existingId=1L;
        nonExistingId=2000L;
        dependentId=3L;
        countTotalProducts=25L;
        dto = Factory.createProductDTO();
        jsonBody = objectMapper.writeValueAsString(dto);
        username = "maria@gmail.com";
        password = "123456";
        bearerToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
    }
    @Test
    public void findAllShouldReturnSortedPageWhenSortedByName()throws Exception{
        mockMvc.perform(get("/products?page=0&size=10sort=name,asc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(countTotalProducts))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
    }
    @Test
    public void updateShouldReturnDtoWhenIdExists()throws Exception{
        String expectName = dto.getName();
        mockMvc.perform(put("/products/{id}",existingId)
                        .header("Authorization", "Bearer " + bearerToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.name").value(expectName));
    }
    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExists()throws Exception{
        mockMvc.perform(put("/products/{id}",nonExistingId)
                        .header("Authorization", "Bearer " + bearerToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
