package com.raf.asmi.karte;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HttpCommunicationUtils {

	public static ResponseEntity<Integer> sendGet(String url) {

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<Integer> response = restTemplate.exchange(url, HttpMethod.GET, entity, Integer.class);

		return response;
	}

	public static ResponseEntity<Integer> sendPost(String url, HttpEntity<String> entity ) {

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Integer> response = restTemplate.exchange(url, HttpMethod.POST, entity, Integer.class);

		return response;
	}
	
	public static ResponseEntity<String> sendPostExpectString(String url, HttpEntity<String> entity ) {

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		return response;
	}

}
