package com.raf.asmi.karte.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// @Configuration
// @EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
	
	private CustomAuthenticationProvider customAuthenticationProvider;
	private BCryptPasswordEncoder encoder;

	
	// Encoder prouzrokuje gresku
	/*
	 * 
	 * Exception in thread "task-2" org.springframework.beans.factory.BeanCreationNotAllowedException: 
	 * Error creating bean with name 'delegatingApplicationListener': 
	 * Singleton bean creation not allowed while singletons of this factory are in destruction 
	 * (Do not request a bean from a BeanFactory in a destroy method implementation!)
	 */
	public WebSecurity(CustomAuthenticationProvider customAuthenticationProvider/*, BCryptPasswordEncoder encoder */){
		super();
		this.customAuthenticationProvider=customAuthenticationProvider;
		//this.encoder = encoder;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    // http.httpBasic().disable();
		
		http.cors().and().csrf().disable().authorizeRequests().antMatchers("/login*").permitAll()
		.anyRequest().authenticated().and().addFilter(new JWTAuthenticationFilter(authenticationManager()))
		.addFilter(new JWTAuthorizationFilter(authenticationManager())).sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "PATCH"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.addExposedHeader("Authorization");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {

		//customAuthenticationProvider.setEncoder(encoder);
		auth.authenticationProvider(customAuthenticationProvider);
	}
}
