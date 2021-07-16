package com.eudes.dscatalog.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.eudes.dscatalog.entities.User;

public class UserDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	@NotBlank(message = "Campo obrigatório")
	private String firstName;
	private String lastName;
	@Email(message = "Utilizar um email válido")
	private String email;
//	private String password; A senha não irá transitar pelo DTO
	
	Set<RoleDTO> roles = new HashSet<>();

	public UserDTO() {}

	public UserDTO(Long id, String firstName, String lastName, String email) {
		this.id = id; // O THIS referencia o atributo do objeto para não causar ambiguidade com o parâmetro
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	public UserDTO(User entity) {
		id = entity.getId();
		firstName = entity.getFirstName();
		lastName = entity.getLastName();
		email = entity.getEmail();
		// Acessa a lista de roles do usuário e para cada role do usuário,
		// ele é instanciado e adicionado num objeto roleDto
		entity.getRoles().forEach(role -> this.roles.add(new RoleDTO(role)));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public Set<RoleDTO> getRoles() {
		return roles;
	}
}
