package com.example.demo.features.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.features.order.model.Orders;
import com.example.demo.features.user.model.User;

public interface OrderRepository extends JpaRepository<Orders, Integer>
{
	List<Orders> findOrdersByUser(User user);
}
