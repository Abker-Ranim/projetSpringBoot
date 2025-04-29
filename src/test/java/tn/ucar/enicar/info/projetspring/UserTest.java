package tn.ucar.enicar.info.projetspring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.ucar.enicar.info.projetspring.entities.Role;
import tn.ucar.enicar.info.projetspring.entities.User;
import tn.ucar.enicar.info.projetspring.entities.task;
import tn.ucar.enicar.info.projetspring.repositories.TaskRepository;
import tn.ucar.enicar.info.projetspring.repositories.userRepo;
import tn.ucar.enicar.info.projetspring.sevices.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserTest {

    @Mock
    private userRepo userRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private UserService userService;

    private User volunteer;
    private User nonVolunteer;
    private task task1;
    private task task2;
    private Integer userId = 1;
    private Integer nonVolunteerId = 2;

    @BeforeEach
    void setUp() {
        // Initialiser les objets
        volunteer = new User();
        volunteer.setId(userId);
        volunteer.setEmail("volunteer@example.com");
        volunteer.setRole(Role.VOLUNTARY);
        volunteer.setScore(0);

        nonVolunteer = new User();
        nonVolunteer.setId(nonVolunteerId);
        nonVolunteer.setEmail("nonvolunteer@example.com");
        nonVolunteer.setRole(Role.RESPONSIBLE);
        nonVolunteer.setScore(0);

        task1 = new task();
        task1.setId(101L);
        task1.setNote(5);

        task2 = new task();
        task2.setId(102L);
        task2.setNote(3);
    }

    @Test
    void calculateUserScore_VolunteerWithTasks_ReturnsScore() {
        // Arrange
        List<task> tasks = List.of(task1, task2);
        when(userRepository.findById(userId)).thenReturn(Optional.of(volunteer));
        when(taskRepository.findByVolunteersContaining(volunteer)).thenReturn(tasks);
        when(userRepository.save(volunteer)).thenReturn(volunteer);

        // Act
        var result = userService.calculateUserScore(userId);

        // Assert
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(taskRepository, Mockito.times(1)).findByVolunteersContaining(volunteer);
        Mockito.verify(userRepository, Mockito.times(1)).save(volunteer);
        assertEquals(8, result); // 5 + 3
        assertEquals(8, volunteer.getScore());
    }

    @Test
    void calculateUserScore_NonVolunteer_ReturnsZero() {
        // Arrange
        when(userRepository.findById(nonVolunteerId)).thenReturn(Optional.of(nonVolunteer));

        // Act
        var result = userService.calculateUserScore(nonVolunteerId);

        // Assert
        Mockito.verify(userRepository, Mockito.times(1)).findById(nonVolunteerId);
        Mockito.verify(taskRepository, Mockito.never()).findByVolunteersContaining(any());
        Mockito.verify(userRepository, Mockito.never()).save(any());
        assertEquals(0, result);
        assertEquals(0, nonVolunteer.getScore());
    }

    @Test
    void calculateUserScore_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.calculateUserScore(userId));
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(taskRepository, Mockito.never()).findByVolunteersContaining(any());
        Mockito.verify(userRepository, Mockito.never()).save(any());
    }

    @Test
    void calculateAllVolunteerScores_VolunteersWithTasks_UpdatesScores() {
        // Arrange
        User volunteer2 = new User();
        volunteer2.setId(3);
        volunteer2.setEmail("volunteer2@example.com");
        volunteer2.setRole(Role.VOLUNTARY);
        volunteer2.setScore(0);

        List<User> volunteers = List.of(volunteer, volunteer2);
        List<task> tasks = List.of(task1);

        when(userRepository.findByRole(Role.VOLUNTARY)).thenReturn(volunteers);
        when(userRepository.findById(volunteer.getId())).thenReturn(Optional.of(volunteer));
        when(userRepository.findById(volunteer2.getId())).thenReturn(Optional.of(volunteer2));
        when(taskRepository.findByVolunteersContaining(volunteer)).thenReturn(tasks);
        when(taskRepository.findByVolunteersContaining(volunteer2)).thenReturn(List.of());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        userService.calculateAllVolunteerScores();

        // Assert
        Mockito.verify(userRepository, Mockito.times(1)).findByRole(Role.VOLUNTARY);
        Mockito.verify(userRepository, Mockito.times(2)).findById(anyInt());
        Mockito.verify(taskRepository, Mockito.times(1)).findByVolunteersContaining(volunteer);
        Mockito.verify(taskRepository, Mockito.times(1)).findByVolunteersContaining(volunteer2);
        Mockito.verify(userRepository, Mockito.times(1)).save(volunteer);
        Mockito.verify(userRepository, Mockito.times(1)).save(volunteer2);
        assertEquals(5, volunteer.getScore());
        assertEquals(0, volunteer2.getScore());
    }

    @Test
    void calculateAllVolunteerScores_NoVolunteers_DoesNothing() {
        // Arrange
        when(userRepository.findByRole(Role.VOLUNTARY)).thenReturn(List.of());

        // Act
        userService.calculateAllVolunteerScores();

        // Assert
        Mockito.verify(userRepository, Mockito.times(1)).findByRole(Role.VOLUNTARY);
        Mockito.verify(userRepository, Mockito.never()).findById(anyInt());
        Mockito.verify(taskRepository, Mockito.never()).findByVolunteersContaining(any());
        Mockito.verify(userRepository, Mockito.never()).save(any());
    }

    @Test
    void getLeaderboard_VolunteersWithScores_ReturnsSortedLeaderboard() {
        // Arrange
        User volunteer2 = new User();
        volunteer2.setId(3);
        volunteer2.setEmail("volunteer2@example.com");
        volunteer2.setRole(Role.VOLUNTARY);
        volunteer2.setScore(10);

        volunteer.setScore(5);

        List<User> volunteers = List.of(volunteer2, volunteer);
        when(userRepository.findByRole(Role.VOLUNTARY)).thenReturn(volunteers);

        // Act
        var result = userService.getLeaderboard();

        // Assert
        Mockito.verify(userRepository, Mockito.times(1)).findByRole(Role.VOLUNTARY);
        assertEquals(2, result.size());
        // Premier utilisateur (score 10, rang 1)
        assertEquals("volunteer2@example.com", result.get(0).getEmail());
        assertEquals(10, result.get(0).getScore());
        assertEquals(1, result.get(0).getRank());
        // Deuxième utilisateur (score 5, rang 2)
        assertEquals("volunteer@example.com", result.get(1).getEmail());
        assertEquals(5, result.get(1).getScore());
        assertEquals(2, result.get(1).getRank());
    }

    @Test
    void getLeaderboard_NoVolunteers_ReturnsEmptyList() {
        // Arrange
        when(userRepository.findByRole(Role.VOLUNTARY)).thenReturn(List.of());

        // Act
        var result = userService.getLeaderboard();

        // Assert
        Mockito.verify(userRepository, Mockito.times(1)).findByRole(Role.VOLUNTARY);
        assertEquals(0, result.size());
    }

    @Test
    void getLeaderboard_SameScores_SameRank() {
        // Arrange
        User volunteer2 = new User();
        volunteer2.setId(3);
        volunteer2.setEmail("volunteer2@example.com");
        volunteer2.setRole(Role.VOLUNTARY);
        volunteer2.setScore(5);

        volunteer.setScore(5);

        List<User> volunteers = List.of(volunteer, volunteer2);
        when(userRepository.findByRole(Role.VOLUNTARY)).thenReturn(volunteers);

        // Act
        var result = userService.getLeaderboard();

        // Assert
        Mockito.verify(userRepository, Mockito.times(1)).findByRole(Role.VOLUNTARY);
        assertEquals(2, result.size());
        // Premier utilisateur (score 5, rang 1)
        assertEquals(5, result.get(0).getScore());
        assertEquals(1, result.get(0).getRank());
        // Deuxième utilisateur (score 5, rang 1)
        assertEquals(5, result.get(1).getScore());
        assertEquals(1, result.get(1).getRank());
    }
}