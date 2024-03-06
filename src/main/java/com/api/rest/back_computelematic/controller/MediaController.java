package com.api.rest.back_computelematic.controller;


import com.api.rest.back_computelematic.service.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("media")
@AllArgsConstructor
public class MediaController {

    private final StorageService storageService;

    private final HttpServletRequest request;


    @PostMapping("upload")
    public List<Map<String, String>> uploadFiles(@RequestParam("files") List<MultipartFile> multipartFiles) {
        List<Map<String, String>> responseList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String path = storageService.store(multipartFile);
            String host = request.getRequestURL().toString().replace(request.getRequestURI(), "");
            String url = ServletUriComponentsBuilder
                    .fromHttpUrl(host)
                    .path("/media/")
                    .path(path)
                    .toUriString();

            responseList.add(Map.of("url", url));
        }

        return responseList;
    }


    @GetMapping("{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws IOException {
        Resource file = storageService.loadAsResource(filename);
        String contentType = Files.probeContentType(file.getFile().toPath());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(file);
    }


}
