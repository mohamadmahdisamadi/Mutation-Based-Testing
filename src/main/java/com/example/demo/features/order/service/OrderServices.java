package com.example.demo.features.order.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.features.order.model.Orders;
import com.example.demo.features.order.repository.OrderRepository;
import com.example.demo.features.user.model.User;
@Component
public class OrderServices
{
	private static final BigDecimal MAX_ORDER_TOTAL = new BigDecimal("10000.00");

	@Autowired
	private OrderRepository orderRepository;
	public List<Orders> getOrders()
	{
		List<Orders> list=this.orderRepository.findAll();
		return list;
	}
	public void saveOrder(Orders order)
	{
		this.orderRepository.save(order);
	}

	public void updateOrder(int id,Orders order)
	{
		order.setoId(id);
		this.orderRepository.save(order);
		 
	}

	public void deleteOrder(int id)
	{
		this.orderRepository.deleteById(id);
	}

	public List<Orders> getOrdersForUser(User user)
	{
	 return  this.orderRepository.findOrdersByUser(user);
	}

	public double calculateTotalForUser(User user)
	{
		List<Orders> orders = getOrdersForUser(user);
		if (orders == null)
		{
			return 0.0;
		}

		BigDecimal total = BigDecimal.ZERO;
		for (Orders order : orders)
		{
			if (order == null)
			{
				continue;
			}
			validateOrderLine(order);
			BigDecimal lineTotal = getLineTotal(order);
			total = total.add(lineTotal);
		}
		BigDecimal roundedTotal = roundToCurrency(total);
		if (roundedTotal.compareTo(MAX_ORDER_TOTAL) > 0)
		{
			throw new IllegalArgumentException("Order total exceeds maximum allowed value");
		}
		return roundedTotal.doubleValue();
	}

	private void validateOrderLine(Orders order)
	{
		if (order.getoQuantity() <= 0)
		{
			throw new IllegalArgumentException("Quantity must be positive");
		}
		if (order.getoPrice() < 0)
		{
			throw new IllegalArgumentException("Price cannot be negative");
		}
	}

	private BigDecimal getLineTotal(Orders order)
	{
		double totalAmount = order.getTotalAmmout();
		if (totalAmount > 0)
		{
			return BigDecimal.valueOf(totalAmount);
		}
		BigDecimal price = BigDecimal.valueOf(order.getoPrice());
		BigDecimal quantity = BigDecimal.valueOf(order.getoQuantity());
		return price.multiply(quantity);
	}

	private BigDecimal roundToCurrency(BigDecimal value)
	{
		return value.setScale(2, RoundingMode.HALF_UP);
	}
	
}
