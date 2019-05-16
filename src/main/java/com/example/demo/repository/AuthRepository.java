package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.UserInformation;

public interface AuthRepository extends MongoRepository<UserInformation, Integer>{

	public UserInformation findByEmailId(String emailId);
	
}
