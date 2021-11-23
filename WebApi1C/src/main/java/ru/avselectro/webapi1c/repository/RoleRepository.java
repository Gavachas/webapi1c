package ru.avselectro.webapi1c.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.avselectro.webapi1c.domain.WebAPIRole;

public interface RoleRepository extends JpaRepository<WebAPIRole, Integer>{
	WebAPIRole findByName(String name);
}
