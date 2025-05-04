package tn.ucar.enicar.info.projetspring.sevices;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.ucar.enicar.info.projetspring.entities.event;
import tn.ucar.enicar.info.projetspring.repositories.EventRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final String uploadDir = "uploads/images/";

    public event createEvent(event event, MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            String imagePath = saveImageFile(image);
            event.setImagePath(imagePath);
        }
        return eventRepository.save(event);
    }

    public event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
    }

    public List<event> getAllEvents() {
        return eventRepository.findAll();
    }

    public event updateEvent(Long eventId, event updatedEvent, MultipartFile image) {
        event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setStartDate(updatedEvent.getStartDate());
        existingEvent.setEndDate(updatedEvent.getEndDate());
        existingEvent.setParticipants(updatedEvent.getParticipants());
        existingEvent.setLocation(updatedEvent.getLocation());
        existingEvent.setOrganization(updatedEvent.getOrganization());
        existingEvent.setVision(updatedEvent.getVision());

        if (image != null && !image.isEmpty()) {
            String imagePath = saveImageFile(image);
            existingEvent.setImagePath(imagePath);
        } else if (updatedEvent.getImagePath() != null) {
            existingEvent.setImagePath(updatedEvent.getImagePath());
        }

        return eventRepository.save(existingEvent);
    }

    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Event not found");
        }
        eventRepository.deleteById(eventId);
    }

    private String saveImageFile(MultipartFile image) {
        try {
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            String originalFileName = image.getOriginalFilename();
            String fileExtension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".")) : ".jpg";
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            Path filePath = Paths.get(uploadDir, uniqueFileName);

            Files.write(filePath, image.getBytes());
            // Normaliser le chemin avec des barres obliques
            String normalizedPath = "uploads/images/" + uniqueFileName;
            System.out.println("Saved image path: " + normalizedPath); // Log pour déboguer
            return normalizedPath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image file", e);
        }
    }
}