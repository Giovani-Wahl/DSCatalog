package com.giovaniwahl.dscatalog.repositories;

import com.giovaniwahl.dscatalog.entities.PasswordRecover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordRecoverRepository extends JpaRepository<PasswordRecover,Long> {
}
