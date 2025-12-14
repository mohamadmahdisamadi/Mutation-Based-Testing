package com.example.demo.features.product.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_table")
public class Product
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int pid;
	private String pname;
	private double pprice;
	private String pdescription;
	private int defaultServingSize = 1;
	private int initialStock = 0;

	@Embedded
	private NutritionProfile nutritionProfile;

	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public double getPprice() {
		return pprice;
	}
	public void setPprice(double pprice) {
		this.pprice = pprice;
	}
	public String getPdescription() {
		return pdescription;
	}
	public void setPdescription(String pdescription) {
		this.pdescription = pdescription;
	}
	@Override
	public String toString() {
		return "Product [pid=" + pid + ", pname=" + pname + ", pprice=" + pprice + ", pdescription=" + pdescription
				+ "]";
	}

	public int getDefaultServingSize() {
		return defaultServingSize;
	}

	public void setDefaultServingSize(int defaultServingSize) {
		this.defaultServingSize = defaultServingSize;
	}

	public int getInitialStock() {
		return initialStock;
	}

	public void setInitialStock(int initialStock) {
		this.initialStock = initialStock;
	}

	public NutritionProfile getNutritionProfile() {
		return nutritionProfile;
	}

	public void setNutritionProfile(NutritionProfile nutritionProfile) {
		this.nutritionProfile = nutritionProfile;
	}

}
