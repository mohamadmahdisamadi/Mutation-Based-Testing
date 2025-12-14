package com.example.demo.features.admin.repository;


import org.springframework.data.repository.CrudRepository;

import com.example.demo.features.admin.model.Admin;

public interface AdminRepository extends CrudRepository<Admin, Integer>
{
	public Admin findByAdminEmail(String email);
}
