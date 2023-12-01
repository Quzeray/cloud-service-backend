package com.example.cloudservicebackend.controller;

import com.example.cloudservicebackend.entity.CloudFile;
import com.example.cloudservicebackend.model.ModelMapper;
import com.example.cloudservicebackend.model.request.FileNameRequest;
import com.example.cloudservicebackend.model.response.FileResponse;
import com.example.cloudservicebackend.service.CloudFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.cloudservicebackend.model.ModelMapper.mapToFileResponse;


@Log4j2
@RestController
@RequiredArgsConstructor
public class CloudFileController {
    private final CloudFileService cloudFileService;

    @GetMapping("/list")
    public ResponseEntity<?> getFileList(@RequestHeader("auth-token") String authToken,
                                         @RequestParam(name = "limit") int limit) {
        List<CloudFile> files = cloudFileService.getFileList(limit, authToken);
        List<FileResponse> fileResponseList = files.stream()
                .map(ModelMapper::mapToFileResponse)
                .toList();
        return ResponseEntity.ok(fileResponseList);
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String fileName,
                                        @RequestBody MultipartFile file) {
        cloudFileService.uploadFile(fileName, file, authToken);
        return ResponseEntity.ok("Success upload");
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String fileName) {
        cloudFileService.deleteFile(fileName, authToken);
        return ResponseEntity.ok("Success deleted");
    }

    @GetMapping("/file")
    public ResponseEntity<?> downloadFile(@RequestHeader("auth-token") String authToken,
                                          @RequestParam("filename") String fileName) {

        CloudFile cloudFile = cloudFileService.downloadFile(fileName, authToken);
        FileResponse fileResponse = mapToFileResponse(cloudFile);
        return ResponseEntity.ok(fileResponse);
    }

    @PutMapping("/file")
    public ResponseEntity<?> editFileName(@RequestHeader("auth-token") String authToken,
                                          @RequestParam("filename") String fileName,
                                          @RequestBody FileNameRequest fileNameRequest) {
        final String newFileName = fileNameRequest.getFileName();
        cloudFileService.editFileName(fileName, newFileName, authToken);
        return ResponseEntity.ok("Success upload");
    }
}

