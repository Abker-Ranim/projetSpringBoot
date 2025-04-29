package tn.ucar.enicar.info.projetspring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.ucar.enicar.info.projetspring.auth.CommentRequestDTO;
import tn.ucar.enicar.info.projetspring.entities.comment;
import tn.ucar.enicar.info.projetspring.entities.task;
import tn.ucar.enicar.info.projetspring.entities.User;
import tn.ucar.enicar.info.projetspring.repositories.CommentRepository;
import tn.ucar.enicar.info.projetspring.repositories.TaskRepository;
import tn.ucar.enicar.info.projetspring.repositories.userRepo;
import tn.ucar.enicar.info.projetspring.sevices.CommentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private userRepo userRepository;

    @InjectMocks
    private CommentService commentService;

    private CommentRequestDTO request;
    private User user;
    private task task;
    private comment comment;
    private Long taskId = 1L;
    private Integer userId = Math.toIntExact(2L);
    private Long commentId = 3L;
    private LocalDateTime createdAt;

    @BeforeEach
    void setUp() {
        // Initialiser les objets
        createdAt = LocalDateTime.now();

        user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");

        task = new task();
        task.setId(taskId);
        task.setResponsible(user); // L'utilisateur est le responsable

        comment = comment.builder()
                .id(commentId)
                .content("Test comment")
                .task(task)
                .user(user)
                .createdAt(createdAt)
                .build();

        request = new CommentRequestDTO();
        request.setTaskId(taskId);
        request.setContent("Test comment");
    }

    @Test
    void createComment_Success_ReturnsCommentResponseDTO() {
        // Arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(commentRepository.save(any(comment.class))).thenReturn(comment);

        // Act
        var result = commentService.createComment(request, user);

        // Assert
        Mockito.verify(taskRepository, Mockito.times(1)).findById(taskId);
        Mockito.verify(commentRepository, Mockito.times(1)).save(any(comment.class));
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getContent(), result.getContent());
        assertEquals(taskId, result.getTaskId());
        assertEquals(user.getEmail(), result.getUserEmail());
        assertEquals(comment.getCreatedAt(), result.getCreatedAt());
    }

    @Test
    void createComment_TaskNotFound_ThrowsException() {
        // Arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commentService.createComment(request, user));
        Mockito.verify(taskRepository, Mockito.times(1)).findById(taskId);
        Mockito.verify(commentRepository, Mockito.never()).save(any(comment.class));
    }

    @Test
    void createComment_UnauthorizedUser_ThrowsException() {
        // Arrange
        User unauthorizedUser = new User();
        unauthorizedUser.setId(999); // Utilisateur non responsable ni volontaire
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commentService.createComment(request, unauthorizedUser));
        Mockito.verify(taskRepository, Mockito.times(1)).findById(taskId);
        Mockito.verify(commentRepository, Mockito.never()).save(any(comment.class));
    }

    @Test
    void getCommentsByTaskId_Success_ReturnsCommentList() {
        // Arrange
        List<comment> comments = List.of(comment);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(commentRepository.findByTaskId(taskId)).thenReturn(comments);

        // Act
        var result = commentService.getCommentsByTaskId(taskId);

        // Assert
        Mockito.verify(taskRepository, Mockito.times(1)).findById(taskId);
        Mockito.verify(commentRepository, Mockito.times(1)).findByTaskId(taskId);
        assertEquals(comments.size(), result.size());
        assertEquals(comment.getId(), result.get(0).getId());
        assertEquals(comment.getContent(), result.get(0).getContent());
        assertEquals(taskId, result.get(0).getTaskId());
        assertEquals(user.getEmail(), result.get(0).getUserEmail());
    }

    @Test
    void getCommentsByTaskId_TaskNotFound_ThrowsException() {
        // Arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commentService.getCommentsByTaskId(taskId));
        Mockito.verify(taskRepository, Mockito.times(1)).findById(taskId);
        Mockito.verify(commentRepository, Mockito.never()).findByTaskId(taskId);
    }
}