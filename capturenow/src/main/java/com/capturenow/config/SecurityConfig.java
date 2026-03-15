package com.capturenow.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.capturenow.filter.JwtAuthFilter;
import com.capturenow.serviceimpl.CustomerUserDetailsService;
import com.capturenow.serviceimpl.PhotographerUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;
//import lombok.var;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private JwtAuthFilter authFilter;

	@Value("${app.cors.origins}")
	private List<String> allowedOrigins;


	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomerUserDetailsService();
	}
	
	@Bean
	public UserDetailsService photographerDetailsService() {
		return new PhotographerUserDetailsService();
	}

	@Bean
	@Order(1)
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	{
		return http.csrf().disable()
				.cors(Customizer.withDefaults())
				.securityMatcher(AntPathRequestMatcher.antMatcher("/customer/**"))
				.authorizeHttpRequests()
				.requestMatchers("/customer/signup","/customer/signin","/customer/authtoken","/customer/validate",
						"/photographer/signup","/photographer/validate","/photographer/signin","/photographer/authtoken",
						"/customer/getPhotographersIndex/{offset}/{pageSize}", "/customer/addFilter/{offset}/{pageSize}/{field}",
						"/customer/forgotPasswordOtp","/customer/forgotPassword", "/customer/searchByPreference", "/customer/health")
				.permitAll()
				.and()
				.authorizeHttpRequests().requestMatchers("/customer/getPhotographers/{offset}/{pageSize}","/customer/getPhotographerById",
						"/customer/getEquipmentsById", "/customer/getAlbumsById","/customer/addReview","/customer/getReviewsById", "/customer/deleteReviewsById",
						"/customer/getPackages","/customer/createBooking", "/customer/getBookingStatus", "/customer/updateDetails",
						"/customer/searchByLocation", "/customer/resetPasswordOtp", "/customer/resetPassword", "/customer/changePhoto",
						"/customer/addToFavorites", "/customer/removeFromFavorites","/customer/getAllFavorites","/customer/cancelBooking")
				.authenticated().and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authenticationProvider(authenticationProviderC())
				.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class).build();
	}
	
	@Bean
	@Order(2)
	public SecurityFilterChain securityFilterChain2(HttpSecurity http) throws Exception
	{
		return http.csrf().disable()
				.cors(Customizer.withDefaults())
				.securityMatcher(AntPathRequestMatcher.antMatcher("/photographer/**"))
				.authorizeHttpRequests()
				.requestMatchers("/customer/signup","/customer/authtoken","/customer/validate","/photographer/signup",
						"/photographer/validate","/photographer/Login","/photographer/authtoken","/photographer/searchPhotographers",
						"/photographer/forgotPassword","/photographer/forgotPasswordOtp")
				.permitAll()
				.and()
				.authorizeHttpRequests().requestMatchers("/photographer/addAlbums",
						"/photographer/deletePackage","/photographer/getPackages","/photographer/updateProfileInfo",
						"/photographer/addPackage","/photographer/getAlbums","/photographer/getEquipment","/photographer/changePhoto",
						"/photographer/deletePhoto","/photographer/resetPasswordOtp", "/photographer/resetPassword",
						"/photographer/getReviews", "/photographer/getBookingStatus", "/photographer/acceptDeclineBooking",
						"/photographer/addKycDetails")
				.authenticated().and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authenticationProvider(authenticationProviderP())
				.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class).build();
	}
	
	@Bean
	public AuthenticationProvider authenticationProviderC() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
	
	@Bean
	public AuthenticationProvider authenticationProviderP() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(photographerDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Qualifier("customer")
	@Bean
	public AuthenticationManager authenticationManagerC(UserDetailsService userDetailsService) throws Exception {
		var authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return new ProviderManager(authProvider);
	}
	
	@Primary
	@Qualifier("photographer")
	@Bean
	public AuthenticationManager authenticationManagerP(UserDetailsService userDetailsService) throws Exception {
		var authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(photographerDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return new ProviderManager(authProvider);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource(HttpServletRequest request) {
		CorsConfiguration configuration = new CorsConfiguration();

		// Allow multiple origins
		configuration.setAllowedOrigins(allowedOrigins);

		configuration.setAllowedHeaders(List.of("*"));

		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

		// Allow credentials if needed
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
