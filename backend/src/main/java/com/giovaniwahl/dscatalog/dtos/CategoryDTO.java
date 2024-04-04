package com.giovaniwahl.dscatalog.dtos;

import com.giovaniwahl.dscatalog.entities.Category;

import java.time.Instant;

public class CategoryDTO {
    private Long id;
    private String name;
    private Instant createdAt;
    private Instant updateAt;

    public CategoryDTO() {
    }

    public CategoryDTO(Long id, String name, Instant createdAt, Instant updateAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
    }
    public CategoryDTO(Category entity) {
        id = entity.getId();
        name = entity.getName();
        createdAt = entity.getCreatedAt();
        updateAt = entity.getUpdateAt();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {return createdAt;}

    public Instant getUpdateAt() {return updateAt;}
}
