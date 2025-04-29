package tn.ucar.enicar.info.projetspring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.ucar.enicar.info.projetspring.entities.event;
import tn.ucar.enicar.info.projetspring.repositories.EventRepository;
import tn.ucar.enicar.info.projetspring.sevices.EventService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private event event;
    private Long eventId = 5L;

    @BeforeEach
    void setUp() {
        event = new event();
        event.setId(eventId);
        event.setTitle("Conférence annuelle");
        event.setDescription("Une conférence sur les nouvelles technologies.");
        LocalDateTime startDateTime = LocalDateTime.of(2025, 6, 1, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 6, 2, 18, 0);
        event.setStartDate(Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        event.setEndDate(Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        event.setLocation("Tunis");
    }

    @Test
    void createEvent_Success_ReturnsSavedEvent() {
        // Arrange
        when(eventRepository.save(any(event.class))).thenReturn(event);

        // Act
        var result = eventService.createEvent(event, null); // Image null pour éviter saveImageFile

        // Assert
        Mockito.verify(eventRepository, Mockito.times(1)).save(event);
        assertEquals(event.getId(), result.getId());
        assertEquals(event.getTitle(), result.getTitle());
        assertEquals(event.getLocation(), result.getLocation());
    }

    @Test
    void getEventById_Success_ReturnsEvent() {
        // Arrange
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        var result = eventService.getEventById(eventId);

        // Assert
        Mockito.verify(eventRepository, Mockito.times(1)).findById(eventId);
        assertEquals(event.getId(), result.getId());
        assertEquals(event.getTitle(), result.getTitle());
        assertEquals(event.getLocation(), result.getLocation());
    }

    @Test
    void getEventById_NotFound_ThrowsException() {
        // Arrange
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> eventService.getEventById(eventId));
        Mockito.verify(eventRepository, Mockito.times(1)).findById(eventId);
    }

    @Test
    void getAllEvents_Success_ReturnsEventList() {
        // Arrange
        List<event> events = List.of(event);
        when(eventRepository.findAll()).thenReturn(events);

        // Act
        var result = eventService.getAllEvents();

        // Assert
        Mockito.verify(eventRepository, Mockito.times(1)).findAll();
        assertEquals(events.size(), result.size());
        assertEquals(event.getId(), result.get(0).getId());
        assertEquals(event.getTitle(), result.get(0).getTitle());
    }

    @Test
    void updateEvent_Success_ReturnsUpdatedEvent() {
        // Arrange
        event updatedEvent = new event();
        updatedEvent.setTitle("Conférence mise à jour");
        updatedEvent.setDescription("Description mise à jour");
        updatedEvent.setLocation("Sousse");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(event.class))).thenReturn(event);

        // Act
        var result = eventService.updateEvent(eventId, updatedEvent, null); // Image null pour éviter saveImageFile

        // Assert
        Mockito.verify(eventRepository, Mockito.times(1)).findById(eventId);
        Mockito.verify(eventRepository, Mockito.times(1)).save(event);
        assertEquals(updatedEvent.getTitle(), result.getTitle());
        assertEquals(updatedEvent.getLocation(), result.getLocation());
    }

    @Test
    void updateEvent_NotFound_ThrowsException() {
        // Arrange
        event updatedEvent = new event();
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> eventService.updateEvent(eventId, updatedEvent, null));
        Mockito.verify(eventRepository, Mockito.times(1)).findById(eventId);
        Mockito.verify(eventRepository, Mockito.never()).save(any(event.class));
    }

    @Test
    void deleteEvent_Success_DeletesEvent() {
        // Arrange
        when(eventRepository.existsById(eventId)).thenReturn(true);

        // Act
        eventService.deleteEvent(eventId);

        // Assert
        Mockito.verify(eventRepository, Mockito.times(1)).existsById(eventId);
        Mockito.verify(eventRepository, Mockito.times(1)).deleteById(eventId);
    }

    @Test
    void deleteEvent_NotFound_ThrowsException() {
        // Arrange
        when(eventRepository.existsById(eventId)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> eventService.deleteEvent(eventId));
        Mockito.verify(eventRepository, Mockito.times(1)).existsById(eventId);
        Mockito.verify(eventRepository, Mockito.never()).deleteById(eventId);
    }
}