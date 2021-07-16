// Tipo genérico que estende a INTERFACE REPOSITORY JPA já traz um
// monte de operações prontas para sereme excutadas diretamente no BD
// tudo que for implementado funcionará em qualquer BD relacional

package com.eudes.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eudes.dscatalog.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	User findByEmail(String email);
}
