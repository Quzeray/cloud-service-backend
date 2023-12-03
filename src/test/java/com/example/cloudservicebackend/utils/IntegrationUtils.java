package com.example.cloudservicebackend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class IntegrationUtils {
    private static final Random RANDOM = new Random();
    private static final String DATE_FORMAT = "yyyyMMdd_HHmmssSSS";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static JsonNode jsonNode(String string) {
        JsonNode responseBody;
        try {
            responseBody = OBJECT_MAPPER.readTree(string);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return responseBody;
    }

    public static HttpEntity<MultiValueMap<String, Object>> createRequestEntityFile(String filename, HttpHeaders headers) {
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        byte[] fileBytes = filename.getBytes();
        ByteArrayResource fileResource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new HttpEntity<>(fileResource));

        return new HttpEntity<>(body, headers);
    }

    public static HttpEntity<String> createRequestRenameEntity(String filename, HttpHeaders headers) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject renameJson = new JSONObject();
        renameJson.put("filename", filename);
        return new HttpEntity<>(renameJson.toString(), headers);
    }

    public static HttpHeaders createHeaders(String authToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("auth-token", authToken);
        return headers;
    }

    public static String generateUniqueFileName() {
        String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        int randomSuffix = RANDOM.nextInt(1000);
        return "file_" + timestamp + "_" + randomSuffix;
    }
}
