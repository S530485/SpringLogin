package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.UserSecureInfo;

public interface ConfRepository extends MongoRepository<UserSecureInfo, String>{
	
	public UserSecureInfo findByPassword(String password);

}