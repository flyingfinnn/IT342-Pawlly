package com.g1appdev.Hubbits.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class MicrosoftGraphService {

    private static final String GRAPH_API_URL = "https://graph.microsoft.com/v1.0/me";

    public MicrosoftUser validateTokenAndGetUser(String token) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                GRAPH_API_URL,
                HttpMethod.GET,
                entity,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> body = response.getBody();
                MicrosoftUser user = new MicrosoftUser();
                user.setFirstName((String) body.get("givenName"));
                user.setLastName((String) body.get("surname"));
                user.setEmail((String) body.get("mail"));
                return user;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}