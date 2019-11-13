package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query(value = "select u from User u join u.roles r where r.role=:role")
    List<User> findAllUsingRole(@Param("role") String userRole);

}