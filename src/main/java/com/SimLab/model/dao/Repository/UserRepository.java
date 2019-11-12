package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    @Query(value = "SELECT u.Id FROM User u WHERE u.email = :email")
    public int Getuserid(@Param("email") String email);

}