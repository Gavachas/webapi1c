package ru.avselectro.webapi1c.api;


import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import java.net.URI;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.avselectro.webapi1c.domain.WebAPIRequest;
import ru.avselectro.webapi1c.domain.WebAPIRole;
import ru.avselectro.webapi1c.domain.WebAPIUser;
import ru.avselectro.webapi1c.service.UserService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserResource {
	private final UserService userService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@GetMapping("/users")
	public ResponseEntity<List<WebAPIUser>>getUsers() {
		return ResponseEntity.ok().body(userService.getUsers());
	}
	
	@PostMapping("/user/save")
	public ResponseEntity<WebAPIUser> saveUser(@RequestBody WebAPIUser user) {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
		return ResponseEntity.created(uri).body(userService.saveUser(user));
	}
	
	@PostMapping("/role/save")
	public ResponseEntity<WebAPIRole> saveRole(@RequestBody WebAPIRole role) {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
		return ResponseEntity.created(uri).body(userService.saveRole(role));
	}
	
	@PostMapping("/role/addtoUser")
	public ResponseEntity<?>addRoleToUser(@RequestBody RoleToUserForm form) {
		userService.addRoleToUser(form.getUsername(), form.getRolename());
		return ResponseEntity.ok().build();
	}	
	
	@GetMapping("/token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException {
		String authorizationHeader = request.getHeader(AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			try {
				String refresh_token = authorizationHeader.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256("htpretyvcmewotvb".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = verifier.verify(refresh_token);
				String username = decodedJWT.getSubject();
				WebAPIUser user = userService.getUser(username);
				String access_token = JWT.create().withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
						.withIssuer(request.getRequestURL().toString())
						.withClaim("roles", user.getRoles().stream().map(WebAPIRole::getName).collect(Collectors.toList()))
						.sign(algorithm);

				Map<String, String> tokens = new HashMap<>();
				tokens.put("access_token", access_token);
				tokens.put("refresh_token", refresh_token);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);

			} catch (Exception exception) {
				
				response.setHeader("error", exception.getMessage());
				response.setStatus(403);
				// FORBIDDEN.value()
				// response.sendError(403);
				Map<String, String> error = new HashMap<>();
				error.put("error_message", exception.getMessage());
				
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
		} else {
			throw new RuntimeException("Refresh token is missing");

		}
	}
	
	@PostMapping("/request/write")
	public void runRequestToDB (HttpServletRequest request, HttpServletResponse response, @RequestBody String query) throws JsonGenerationException, JsonMappingException, IOException  {
		String ip = request.getRemoteAddr();
		Map<String, List<String>> result = new HashMap<>();
		
		RowCallbackHandler rowCallbaskHandler = new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				ArrayList<String> rowData = new ArrayList<>();
				int columns = rs.getMetaData().getColumnCount();
				 for (int i = 1; i <= columns; i++){

					rowData.add(rs.getString(i));
				 }
				 result.put(String.valueOf(rs.getRow()), rowData);
			}

		};
		
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		jdbcTemplate.query(query, rowCallbaskHandler);
		WebAPIUser user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
		WebAPIRequest webreq = new WebAPIRequest();		
		webreq.setIp(ip);
		webreq.setUser(user);
		webreq.setRequest(query);
		webreq.setModifiedDate(new Timestamp(System.currentTimeMillis()));
		
		
		//WebAPIRequest webreq = new WebAPIRequest(user, query, ip);
		userService.addRequest(webreq);
		
		
		try {
			
			new ObjectMapper().writeValue(response.getOutputStream(), result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Map<String, String> error = new HashMap<>();
			error.put("error_message", e.getMessage());
			
			new ObjectMapper().writeValue(response.getOutputStream(), e);

		};	
	}
}

@Data
class RoleToUserForm{
	private String username;
	private String rolename;
}