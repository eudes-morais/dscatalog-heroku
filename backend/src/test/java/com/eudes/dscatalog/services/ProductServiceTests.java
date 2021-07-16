package com.eudes.dscatalog.services;

import static org.mockito.ArgumentMatchers.any; // Import estático

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.eudes.dscatalog.dto.ProductDTO;
import com.eudes.dscatalog.entities.Product;
import com.eudes.dscatalog.repositories.ProductRepository;
import com.eudes.dscatalog.services.exceptions.DatabaseException;
import com.eudes.dscatalog.services.exceptions.ResourceNotFoundException;
import com.eudes.dscatalog.tests.Factory;

// Teste de unidade. Utiliza-se o mockito. Valida um componente específico
@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private PageImpl<Product> page;
	private Product product;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		// As variáveis abaixo são para instanciar uma/várias páginas que serão
		// exibidas pelo método FINDALL do REPOSITORY na classe PRODUCTSERVICE
		product = Factory.createProduct();
		page = new PageImpl<>(List.of(product));
		
		// Simulação de métodos que retornam algo (diferente de VOID)
		Mockito.when(repository.findAll((Pageable)any())).thenReturn(page);
		Mockito.when(repository.save(any())).thenReturn(product);
		// // Abaixo, por causa do REPOSITORY.FINDBYID da CLASSE PRODUCTSERVICE, são duas
		// // situações (optional): Uma caso exista o ID e outra caso não exista o ID
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));  
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		Mockito.when(repository.find(any(), any(), any())).thenReturn(page);
		
		// Simulação de métodos VOID
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		ProductDTO result = service.findById(existingId);
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
		
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(0L, "", pageable);
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void deleteShoulThrowDataBaseExceptionWhenDoNotihngWhenIdDoesNotExists() {
		
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldDoNotihngWhenIdExists() {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
		
		Mockito.verify(repository).deleteById(nonExistingId);
	}
}
