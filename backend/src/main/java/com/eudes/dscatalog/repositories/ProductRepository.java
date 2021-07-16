package com.eudes.dscatalog.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eudes.dscatalog.entities.Category;
import com.eudes.dscatalog.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

	@Query("SELECT DISTINCT obj FROM Product obj "
			+ "JOIN obj.categories cats "
			+ "WHERE (COALESCE(:categories) IS NULL OR cats IN :categories)"
			+ "AND (LOWER(obj.name) LIKE LOWER(CONCAT('%', :name, '%')))") // Forma normal conjuntiva
	Page<Product> find(List<Category> categories, String name, Pageable pageable);
	
	@Query("SELECT obj FROM Product obj "
			+ "JOIN FETCH obj.categories "
			+ "WHERE obj IN :products")	
	List<Product> findProductsWithCategories(List<Product> products);
}
