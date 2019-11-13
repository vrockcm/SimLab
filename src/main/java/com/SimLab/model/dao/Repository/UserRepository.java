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

    @Query("SELECT x.name FROM User x WHERE x.role = student")
    public List<User> getAllStudents();

    @Query("SELECT x.name FROM User x WHERE x.role = instructor")
    public List<User> getAllInstructors();

}