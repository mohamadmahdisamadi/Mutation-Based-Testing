package com.example.demo.features.product.repository;


import org.springframework.data.repository.CrudRepository;

import com.example.demo.features.product.model.Product;

public interface ProductRepository extends CrudRepository<Product,Integer>
{
	public Product findByPname(String name);

}
