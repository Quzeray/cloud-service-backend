package com.example.cloudservicebackend.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileNameRequest {
    @JsonProperty("filename")
    private String fileName;
}
