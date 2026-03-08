package com.capturenow.module;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import org.springframework.stereotype.Component;

@Entity
@Data
@ToString(exclude = {"albums", "packages", "bookings", "photographerRatings"})
@Component
public class Photographer implements UserDetails{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -785199701875466283L;

	@Id
	private String id;
	
	@Column(nullable = false)
	private String name;//required
	
	@Column(nullable = false)
	private String email;//required
	
	@Column(nullable = false)
	private String password;//required
	
	private long phoneNumber;

	private Double startsWith;

	private Double avgRating;

	private String serviceLocation;

	private boolean status;

	private Date signupDateTime;

	private int signupVerificationKey;

	private int resetPasswordVerificationKey;

	private Date resetPasswordReqDateTime;
	
	private boolean isLogin;

	private int experience;//required
	
	@Lob
	@Column(name = "profile_photo")
	private byte[] profilePhoto;//required
	
	private String services;//required
	
	private String languages;//required
	
	@Column(length = 5000)
	private String aboutMe;//required
	
	@JsonManagedReference
	@OneToMany(mappedBy = "photographer",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Albums> albums;
	
	@JsonManagedReference
	@OneToMany( mappedBy = "photographer",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Packages> packages;

	@JsonManagedReference
	@OneToMany( mappedBy = "photographer",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Booking> bookings;

	@JsonManagedReference
	@OneToMany( mappedBy = "photographer",fetch = FetchType.LAZY, cascade =  CascadeType.ALL, orphanRemoval = true)
	private List<PhotographerRatings> photographerRatings;

	@JsonManagedReference
	@OneToOne
	private PhotographerKycDetails photographerKycDetails;
	
	private String role = "ROLE_PHOTOGRAPHER";
	
	private String authToken;

	public Photographer(){
		this.id = generateCustomId();
		this.avgRating = 0.0;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return List.of(new SimpleGrantedAuthority(role));
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	private String generateCustomId() {
		// Implement your custom ID generation logic here
		// Example: return UUID.randomUUID().toString();
		// You can use any logic to create a unique identifier
		return "CN-PG" + UUID.randomUUID().toString();
	}

}
