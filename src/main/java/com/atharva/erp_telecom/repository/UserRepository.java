package com.atharva.erp_telecom.repository;

import com.atharva.erp_telecom.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {
    Optional<Users> findByUserName(String userName);
}
