package tn.ucar.enicar.info.projetspring.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.ucar.enicar.info.projetspring.entities.event;
import tn.ucar.enicar.info.projetspring.sevices.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<event> createEvent(
            @RequestPart("event") @Valid event event,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(eventService.createEvent(event, image));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'VOLUNTARY', 'RESPONSIBLE')")
    public ResponseEntity<List<event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'VOLUNTARY', 'RESPONSIBLE')")

    public ResponseEntity<event> getEventById(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEventById(eventId));
    }

    @PutMapping(value = "/{eventId}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<event> updateEvent(
            @PathVariable Long eventId,
            @RequestPart("event") @Valid event event,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(eventService.updateEvent(eventId, event, image));
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}