package com.SimLab.model.dao.Repository;

import com.SimLab.model.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Transactional
    User findById(int id);

//    @Query(value = "select u from User u inner join u.roles r where r.role=:role")
    @Query(value = "SELECT user.user_id, user.active, user.email, user.last_name, user.first_name, user.password " +
            "FROM user INNER JOIN user_role INNER JOIN role WHERE user.user_id=user_role.user_id AND user_role.role_id = role.role_id " +
            "AND role.role = :role", nativeQuery=true)
    List<User> findAllUsingRole(@Param("role") String userRole);

    @Query(value = "SELECT user_role.role_id FROM user_role WHERE user_role.user_id= :userId", nativeQuery = true)
    int findRoleIdByUserID(@Param("userId") int userId);



}