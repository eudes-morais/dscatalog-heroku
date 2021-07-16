package com.eudes.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eudes.dscatalog.dto.CategoryDTO;
import com.eudes.dscatalog.dto.ProductDTO;
import com.eudes.dscatalog.entities.Category;
import com.eudes.dscatalog.entities.Product;
import com.eudes.dscatalog.repositories.CategoryRepository;
import com.eudes.dscatalog.repositories.ProductRepository;
import com.eudes.dscatalog.services.exceptions.DatabaseException;
import com.eudes.dscatalog.services.exceptions.ResourceNotFoundException;

@Service // Annotation que registra a classe como parte do sistema de injeção de dependências do sistema
public class RoleService {
	
	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)	
	public Page<ProductDTO> findAllPaged(Pageable pageable) {
		
		Page<Product> list = repository.findAll(pageable);
		
		return list.map(x -> new ProductDTO(x)); // Como o PAGE já é um STREAM do JAVA 8, não precisa do método STREAm e nem do COLLECT
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		
		Optional<Product> obj = repository.findById(id);
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		
		return new ProductDTO(entity, entity.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		
		Product entity = new Product();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		
		return new ProductDTO(entity);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		
		try {
			Product entity = repository.getOne(id); // GETONE ainda não está 'mexendo' no BD
			copyDtoToEntity(dto, entity);		
			entity = repository.save(entity);
			
			return new ProductDTO(entity);
		}
		catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		}
	}
	
	public void delete(Long id) {
		
		try {
			repository.deleteById(id);
		}
		catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
		
	}
	
	// Método PRIVADO que auxiliará no INSERT e no UPDATE do ProductDTO
	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setDate(dto.getDate());
		entity.setImgUrl(dto.getImgUrl());
		entity.setPrice(dto.getPrice());
		
		entity.getCategories().clear();
		for (CategoryDTO catDto : dto.getCategories()) {
			// O GETONE serve para capturar o ID de uma categoryDTO sem 'tocar' no BD ainda
			Category category = categoryRepository.getOne(catDto.getId());
			entity.getCategories().add(category);
		}
	}
 }
