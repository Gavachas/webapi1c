package ru.avselectro.webapi1c.domain;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class WebAPIRequest {
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
    @OneToOne(fetch = FetchType.EAGER)    
	private WebAPIUser user;
    @Column(nullable = false)
	private String request;
	@Column(nullable = false)
	private Date modifiedDate;
	
	@Column(nullable = false, length = 15)
	
	private String ip;
	 
	public WebAPIRequest(WebAPIUser user, String request, String ip) {
		this.ip = ip;
		this.user = user;
		this.request = request;
		this.modifiedDate = new Date(System.currentTimeMillis());
	}
}
