package ru.avselectro.webapi1c.service;

import ru.avselectro.webapi1c.domain.WebAPIUser;

import java.util.List;

import ru.avselectro.webapi1c.domain.WebAPIRole;

public interface UserService {
	WebAPIUser saveUser(WebAPIUser user);
	WebAPIRole saveRole(WebAPIRole role);
	void addRoleToUser(String username, String roleName);
	WebAPIUser getUser(String username);
	List<WebAPIUser> getUsers();
}
