package tn.ucar.enicar.info.projetspring.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tn.ucar.enicar.info.projetspring.auth.CommentRequestDTO;
import tn.ucar.enicar.info.projetspring.auth.CommentResponseDTO;
import tn.ucar.enicar.info.projetspring.entities.User;
import tn.ucar.enicar.info.projetspring.sevices.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<CommentResponseDTO> createComment(
            @RequestBody @Valid CommentRequestDTO request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(commentService.createComment(request, user));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByTaskId(
            @PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTaskId(taskId));
    }
}