package com.raf.asmi.karte.security;

public class SecurityConstants {
	public static final String SECRET = "Projekat2SecretKey";
	public static final String LETOVI_PATH = "/letovi";
	public static final String LOGIN_PATH = "/login"; 
	
	public static final String TOKEN_PREFIX = "Basic ";
	public static final String HEADER_STRING = "Authorization";
	public static final int TOKEN_EXPIRATION_TIME = 864000; // deset dana
	
}
