package com.uade.tpo.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${upload.dir}")
    private String uploadDir;  // Aquí se inyecta el valor de upload.dir

    public String saveFile(MultipartFile file) throws IOException {
        if (!new File(uploadDir).exists()) {
            Files.createDirectories(Paths.get(uploadDir));
        }
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), filePath);
        return fileName;  // Retornamos el nombre del archivo guardado
    }
}
