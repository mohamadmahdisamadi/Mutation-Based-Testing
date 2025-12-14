package com.example.demo.features.user.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.features.user.model.User;

public interface UserRepository extends CrudRepository<User,Integer>
{
public User findUserByUemail(String email);
}
