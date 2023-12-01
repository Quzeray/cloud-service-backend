package com.example.cloudservicebackend.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileNameRequest {
    @JsonProperty("filename")
    private String fileName;
}
