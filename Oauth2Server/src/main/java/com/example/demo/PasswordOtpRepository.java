package com.example.demo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.PasswordOtp;

@Repository
public interface PasswordOtpRepository extends MongoRepository<PasswordOtp, Long>{

}
