package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.entity.Image;
import com.enigma.wmb_api.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping(path = "/api/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadImage(@RequestPart(name = "image")MultipartFile multipartFile) {
        Image image = imageService.create(multipartFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(image);
    }

    @GetMapping(path = APIUrl.PRODUCT_IMAGE_DOWNLOAD_API+"{imageId}")
    public ResponseEntity<Resource> downloadImage(@PathVariable(name = "imageId")String id){
        Resource resource = imageService.getById(id);
        String headerValue = String.format("attachment;filename=%s", resource.getFilename());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
}
