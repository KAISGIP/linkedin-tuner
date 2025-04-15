package com.linkedin.tuner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;

@Service
public class LinkedInService {
    
    private static final Logger logger = LoggerFactory.getLogger(LinkedInService.class);
    
    @Value("${linkedin.client.id}")
    private String clientId;
    
    @Value("${linkedin.client.secret}")
    private String clientSecret;
    
    @Value("${linkedin.api.url}")
    private String apiUrl;
    
    @Value("${linkedin.person.id}")
    private String personId;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public LinkedInService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }
    
    public String publishPost(String content) {
        try {
            logger.info("Début de la publication sur LinkedIn");
            
            // Obtenir un token d'accès
            String accessToken = getAccessToken();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("X-Restli-Protocol-Version", "2.0.0");
            
            // Créer la structure de la requête
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("author", "urn:li:person:" + personId);
            requestBody.put("lifecycleState", "PUBLISHED");
            
            Map<String, Object> specificContent = new HashMap<>();
            Map<String, Object> shareContent = new HashMap<>();
            Map<String, Object> shareCommentary = new HashMap<>();
            shareCommentary.put("text", content);
            shareContent.put("shareCommentary", shareCommentary);
            shareContent.put("shareMediaCategory", "NONE");
            specificContent.put("com.linkedin.ugc.ShareContent", shareContent);
            requestBody.put("specificContent", specificContent);
            
            Map<String, Object> visibility = new HashMap<>();
            visibility.put("com.linkedin.ugc.MemberNetworkVisibility", "PUBLIC");
            requestBody.put("visibility", visibility);
            
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            logger.debug("Corps de la requête LinkedIn: {}", requestBodyJson);
            
            HttpEntity<String> request = new HttpEntity<>(requestBodyJson, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                apiUrl + "/ugcPosts",
                request,
                String.class
            );
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Erreur de l'API LinkedIn: {}", response.getStatusCode());
                logger.error("Réponse d'erreur: {}", response.getBody());
                throw new RuntimeException("Erreur de l'API LinkedIn: " + response.getStatusCode() + " - " + response.getBody());
            }
            
            logger.info("Publication LinkedIn réussie");
            return response.getBody();
        } catch (Exception e) {
            logger.error("Erreur lors de la publication sur LinkedIn", e);
            throw new RuntimeException("Erreur lors de la publication sur LinkedIn: " + e.getMessage());
        }
    }
    
    private String getAccessToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            String requestBody = String.format(
                "grant_type=client_credentials&client_id=%s&client_secret=%s",
                clientId,
                clientSecret
            );
            
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                "https://www.linkedin.com/oauth/v2/accessToken",
                request,
                String.class
            );
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Erreur lors de l'obtention du token: {}", response.getBody());
                throw new RuntimeException("Erreur lors de l'obtention du token: " + response.getStatusCode() + " - " + response.getBody());
            }
            
            // Extraire le token de la réponse
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.get("access_token").asText();
        } catch (Exception e) {
            logger.error("Erreur lors de l'obtention du token", e);
            throw new RuntimeException("Erreur lors de l'obtention du token: " + e.getMessage());
        }
    }
} 