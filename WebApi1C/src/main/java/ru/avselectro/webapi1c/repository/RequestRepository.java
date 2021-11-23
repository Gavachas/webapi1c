package ru.avselectro.webapi1c.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.avselectro.webapi1c.domain.WebAPIRequest;

public interface RequestRepository extends JpaRepository<WebAPIRequest, Integer> {
	//WebAPIRequest findByName(String name);
}
