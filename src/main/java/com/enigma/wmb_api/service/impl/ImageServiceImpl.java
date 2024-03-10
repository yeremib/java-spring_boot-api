package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.entity.Image;
import com.enigma.wmb_api.repository.ImageRepository;
import com.enigma.wmb_api.service.ImageService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {
    private Path directoryPath;
    private final ImageRepository imageRepository;

    public ImageServiceImpl(@Value("${wmb_api.multipart.path_location}") String directoryPath,
                            ImageRepository imageRepository) {
        this.directoryPath = Paths.get(directoryPath);
        this.imageRepository = imageRepository;
    }

    @PostConstruct
    public void initDirectory() {
        if(!Files.exists(directoryPath)) {
            try {
                Files.createDirectory(directoryPath);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
    }

    @Override
    public Image create(MultipartFile multipartFile) {
        try{
            if (!List.of("image/jpeg", "image/png", "image/jpg", "image/svg+xml").contains(multipartFile.getContentType()))
                throw new ConstraintViolationException("Invalid Content Type", null);
            String uniqueFileName = System.currentTimeMillis() + "-" + multipartFile.getOriginalFilename();
            Path filePath = directoryPath.resolve(uniqueFileName);
            Files.copy(multipartFile.getInputStream(), filePath);

            Image image = Image.builder()
                    .name(uniqueFileName)
                    .contentType(multipartFile.getContentType())
                    .size(multipartFile.getSize())
                    .path(filePath.toString())
                    .build();
            imageRepository.saveAndFlush(image);

            return image;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Resource getById(String id) {
        try {
            Image image = imageRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "image not found"));
            Path filePath = Paths.get(image.getPath());
            if(!Files.exists(filePath))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "image not found");
            return new UrlResource(filePath.toUri());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            Image image = imageRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "image not found"));
            Path filePath = Paths.get(image.getPath());
            if(!Files.exists(filePath))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "image not found");
            Files.delete(filePath);
            imageRepository.delete(image);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
