package com.example.cloudservicebackend.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FileResponse {
    private final String filename;
    private final int size;
}