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
import com.eudes.dscatalog.entities.Category;
import com.eudes.dscatalog.repositories.CategoryRepository;
import com.eudes.dscatalog.services.exceptions.DatabaseException;
import com.eudes.dscatalog.services.exceptions.ResourceNotFoundException;

@Service // Annotation que registra a classe como parte do sistema de injeção de dependências do sistema
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly = true)	
	public Page<CategoryDTO> findAllPaged(Pageable pageable) {
		
		Page<Category> list = repository.findAll(pageable);
		
		return list.map(x -> new CategoryDTO(x)); // Como o PAGE já é um STREAM do JAVA 8, não precisa do método STREAm e nem do COLLECT
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		
		Optional<Category> obj = repository.findById(id);
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = repository.save(entity);
		
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		
		try {
			Category entity = repository.getOne(id);
			entity.setName(dto.getName());		
			entity = repository.save(entity);
			
			return new CategoryDTO(entity);
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
 }
