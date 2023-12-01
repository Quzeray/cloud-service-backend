package com.example.cloudservicebackend.model;

import com.example.cloudservicebackend.entity.CloudFile;
import com.example.cloudservicebackend.model.response.FileResponse;

public class ModelMapper {
    public static FileResponse mapToFileResponse(CloudFile cloudFile) {
        return FileResponse.builder()
                .filename(cloudFile.getFileName())
                .size(cloudFile.getData().length)
                .build();
    }
}
