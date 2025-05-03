package tn.ucar.enicar.info.projetspring.controllers;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImageController {
    private static final String UPLOAD_DIR = "uploads/images/";

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {
        System.out.println("Requested image: " + filename); // Log pour déboguer
        Path filePath = Paths.get(UPLOAD_DIR, filename).normalize();
        System.out.println("Resolved file path: " + filePath.toString()); // Log pour déboguer
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists() && resource.isReadable()) {
            System.out.println("Image found: " + filename);
            String contentType = "image/jpeg"; // Par défaut
            if (filename.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            } else if (filename.toLowerCase().endsWith(".gif")) {
                contentType = "image/gif";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        }
        System.out.println("Image not found: " + filename);
        return ResponseEntity.notFound().build();
    }
}