package com.giovaniwahl.dscatalog.repositories;

import com.giovaniwahl.dscatalog.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}
