package com.sathya.rest.backend;

import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository;

import com.sathya.rest.backend.product.Product;



@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
	

}
