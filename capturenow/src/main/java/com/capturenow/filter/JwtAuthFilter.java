package com.capturenow.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.capturenow.serviceimpl.CustomerUserDetailsService;
import com.capturenow.serviceimpl.JwtService;
import com.capturenow.serviceimpl.PhotographerUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Component
@Data
@EqualsAndHashCode(callSuper = false)
public class JwtAuthFilter extends OncePerRequestFilter{

	@Autowired
	private final JwtService jwtservice;

	@Autowired
	private final CustomerUserDetailsService cud;
	
	@Autowired
	private final PhotographerUserDetailsService pud;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String userName = null;
		if(authHeader != null && authHeader.startsWith("Bearer"))
		{
			token = authHeader.substring(7);
			userName = jwtservice.extractUsername(token);
//			filterChain.doFilter(request, response);
		}
		
		if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null)
		{
			try {
				UserDetails userDetails = cud.loadUserByUsername(userName);
				if(jwtservice.validatetoken(token, userDetails))
				{
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			}catch(UsernameNotFoundException e) {
				UserDetails photographerDetails = pud.loadUserByUsername(userName);
				if(jwtservice.validatetoken(token, photographerDetails))
				{
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(photographerDetails, null, photographerDetails.getAuthorities());
					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			}
			
		}
		filterChain.doFilter(request, response);
	}

}
