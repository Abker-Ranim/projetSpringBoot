package tn.ucar.enicar.info.projetspring.sevices;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import tn.ucar.enicar.info.projetspring.auth.CommentRequestDTO;
import tn.ucar.enicar.info.projetspring.auth.CommentResponseDTO;
import tn.ucar.enicar.info.projetspring.entities.comment;
import tn.ucar.enicar.info.projetspring.entities.task;
import tn.ucar.enicar.info.projetspring.entities.User;
import tn.ucar.enicar.info.projetspring.repositories.CommentRepository;
import tn.ucar.enicar.info.projetspring.repositories.TaskRepository;
import tn.ucar.enicar.info.projetspring.repositories.userRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final userRepo userRepository;

    @PreAuthorize("hasAnyRole('RESPONSIBLE', 'VOLUNTARY')")
    public CommentResponseDTO createComment(CommentRequestDTO request, User user) {
        // Validate task
        task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // Check if user is authorized (Responsible or Volunteer)
        boolean isResponsible = task.getResponsible() != null && task.getResponsible().getId().equals(user.getId());
        boolean isVolunteer = task.getVolunteers().stream().anyMatch(v -> v.getId().equals(user.getId()));
        if (!isResponsible && !isVolunteer) {
            throw new IllegalArgumentException("User is not authorized to comment on this task");
        }

        // Create comment
        comment Comment = comment.builder()
                .content(request.getContent())
                .task(task)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        comment savedComment = commentRepository.save(Comment);
        return mapToResponseDTO(savedComment);
    }

    public List<CommentResponseDTO> getCommentsByTaskId(Long taskId) {
        // Validate task
        task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        return commentRepository.findByTaskId(taskId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private CommentResponseDTO mapToResponseDTO(comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .taskId(comment.getTask().getId())
                .userEmail(comment.getUser().getEmail())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}