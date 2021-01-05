package com.raf.asmi.karte.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Ovo je bitno servisu1
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {


	private PasswordEncoder encoder;
	// private UserRepository userRepo;

	@Autowired
	public CustomAuthenticationProvider(/*UserRepository userRepo*/) {
		super();
		// this.userRepo = userRepo;
	}

	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		String email = auth.getName();
		String password = auth.getCredentials().toString();

		// User user = userRepo.findByEmail(email);
		/*// Ako usera nema
		if (user == null) {
			throw new BadCredentialsException("Authentication failed");
		}
		*/

		// proveri sifru
		/*
		if (encoder.matches(password, user.getPassword())) {
			return new UsernamePasswordAuthenticationToken(email, password, Collections.EMPTY_LIST);
		}*/
		if(true) {
			return new UsernamePasswordAuthenticationToken(email, password, Collections.EMPTY_LIST);
		}

		throw new BadCredentialsException("Authentication failed");
	}

	@Override
	public boolean supports(Class<?> auth) {
		return auth.equals(UsernamePasswordAuthenticationToken.class);
	}

	public void setEncoder(PasswordEncoder encoder) {
		this.encoder = encoder;
	}
	
}
