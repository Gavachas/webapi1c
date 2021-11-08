package ru.avselectro.webapi1c.domain;

import java.util.ArrayList;
import java.util.Collection;


import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class WebAPIUser {
	 
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String name;
	private String username;
	private String password;
	
	@ManyToMany(fetch = FetchType.EAGER)
	private Collection<WebAPIRole> roles = new ArrayList<>();
	
}
