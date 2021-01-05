package com.raf.asmi.karte.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import java.util.Collections;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;

// Ovo sluzi samo testa radi u ovom servisu
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authenticationManager;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
		try {

			System.out.println("Auth attempted");
			// Dobiti podatke uvek - nasi su vestacki
			//Login_Form user = new ObjectMapper().readValue(req.getInputStream(), Login_Form.class);

			/*UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getEmail(),
					user.getPassword(), Collections.emptyList());*/
			
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("test@test.com",
					"1234", Collections.emptyList());

			return authenticationManager.authenticate(token);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) {

		String email = auth.getName();

		String token = JWT.create().withSubject(email)
				.withClaim("role", "user") // ili role "admin"
				.withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION_TIME))
				.sign(HMAC512(SecurityConstants.SECRET.getBytes()));
		
		
		System.out.println("Succes!");
		res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
	}
}
