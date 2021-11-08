package ru.avselectro.webapi1c.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.avselectro.webapi1c.domain.WebAPIUser;



public interface UserRepository extends JpaRepository<WebAPIUser, Integer> {
	WebAPIUser findByUsername(String username);
}
