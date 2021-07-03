package com.example.demo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.User;

/**
 * 
 * @author Kristijan Georgiev
 * 
 *         UserRepository with custom methods for finding a User by username or
 *         email
 *
 */

@Repository
@Transactional
public interface UserRepository extends MongoRepository<User, Long> {

	User findByUsername(String username);

	User findByEmail(String email);

}
