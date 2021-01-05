package com.raf.asmi.karte.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	// Ovo ce trebati servisu 1 - on ima usere
	//private UserRepository userRepo;

	@Autowired
	public JWTAuthorizationFilter(AuthenticationManager authManager) {
		super(authManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		String token = req.getHeader(SecurityConstants.HEADER_STRING);

		UsernamePasswordAuthenticationToken authentication = getAuthentication(req, token);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(req, res);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, String token) {

		if (token != null) {
			// parsiranje tokena
			DecodedJWT jwt = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes())).build()
					.verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""));

			// subject je email od korisnika i spakovan je u JWT
			String email = jwt.getSubject();
			Claim role = jwt.getClaim("role");
			System.out.println("ROLE "+ role.asString());


			if (email != null) {
				return new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
			}
			return null;
		}
		return null;
	}
}
