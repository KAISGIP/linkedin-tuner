package com.linkedin.tuner.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DeepSeekService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeepSeekService.class);
    
    @Value("${deepseek.api.key}")
    private String apiKey;
    
    @Value("${deepseek.api.url}")
    private String apiUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public DeepSeekService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }
    
    public String correctText(String text) {
        try {
            logger.info("Début de la correction du texte avec DeepSeek");
            logger.debug("URL de l'API: {}", apiUrl);
            
            // Nettoyage du texte
            String cleanedText = text.replaceAll("[\\u0000-\\u001F]", "");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Accept", "application/json");
            
            // Structure de la requête selon la documentation DeepSeek
            String requestBody = String.format(
                "{\"model\": \"deepseek-chat\", \"messages\": [{\"role\": \"user\", \"content\": \"Corrige et améliore ce texte: %s\"}]}",
                cleanedText
            );
            
            logger.debug("Corps de la requête: {}", requestBody);
            
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            logger.debug("Réponse brute de l'API: {}", response.getBody());
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Erreur de l'API DeepSeek: {}", response.getStatusCode());
                throw new RuntimeException("Erreur de l'API DeepSeek: " + response.getStatusCode());
            }
            
            JsonNode root = objectMapper.readTree(response.getBody());
            
            // Extraction du texte corrigé de la réponse
            if (root.has("choices") && root.get("choices").size() > 0) {
                JsonNode firstChoice = root.get("choices").get(0);
                if (firstChoice.has("message") && firstChoice.get("message").has("content")) {
                    String correctedText = firstChoice.get("message").get("content").asText();
                    logger.info("Texte corrigé récupéré avec succès");
                    return correctedText;
                }
            }
            
            logger.warn("Aucun texte corrigé trouvé dans la réponse");
            return text;
        } catch (Exception e) {
            logger.error("Erreur lors de la correction du texte", e);
            throw new RuntimeException("Erreur lors de la correction du texte: " + e.getMessage());
        }
    }
} 