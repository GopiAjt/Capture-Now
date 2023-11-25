package com.capturenow.module;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Photographer implements UserDetails{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -785199701875466283L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(nullable = false)
	private String name;//required
	
	@Column(nullable = false)
	private String email;//required
	
	@Column(nullable = false)
	private String password;//required
	
	private long phoneNumber;
	
	private String serviceLocation;//required
	
	private Date signupDateTime;

	private boolean status;

	private int signupVerificationKey;

	private int resetPasswordVerificationKey;

	private Date resetPasswordReqDateTime;
	
	private boolean isLogin;

	private int experience;//required
	
	@Lob
	@Column(name = "profile_photo", columnDefinition="LONGBLOB")
	private byte[] profilePhoto;//required
	
	private String services;//required
	
	private String languages;//required
	
	@Column(length = 500)
	private String aboutMe;//required
	
	@JsonManagedReference
	@OneToMany(mappedBy = "photographer", cascade = CascadeType.PERSIST)
	private List<Albums> Albums;
	
	@JsonManagedReference
	@OneToMany(mappedBy = "photographer", cascade = CascadeType.PERSIST)
	private List<Packages> Packages;

	@JsonManagedReference
	@OneToMany(mappedBy = "photographer", cascade = CascadeType.PERSIST)
	private List<Booking> bookings;

	@OneToMany(mappedBy = "photographer", cascade =  CascadeType.PERSIST)
	private List<PhotographerRatings> photographerRatings;
	
	private String role = "ROLE_PHOTOGRAPHER";
	
	private String authToken;

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
	
}
