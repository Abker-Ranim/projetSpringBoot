package tn.ucar.enicar.info.projetspring.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tn.ucar.enicar.info.projetspring.entities.TaskDTO;
import tn.ucar.enicar.info.projetspring.entities.User;
import tn.ucar.enicar.info.projetspring.entities.status;
import tn.ucar.enicar.info.projetspring.sevices.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    // Étape 4 : Responsible crée une tâche
    @PostMapping("/create")
    @PreAuthorize("hasRole('RESPONSIBLE')")
    public ResponseEntity<TaskDTO> createTask(
            @RequestBody TaskDTO taskDTO,
            @AuthenticationPrincipal User responsible) {
        return ResponseEntity.ok(taskService.createTask(taskDTO, responsible));
    }
    @GetMapping("/responsible")
    @PreAuthorize("hasRole('RESPONSIBLE')")
    public ResponseEntity<List<TaskDTO>> getTasksByResponsible(@AuthenticationPrincipal User responsible) {
        return ResponseEntity.ok(taskService.getTasksByResponsible(responsible));
    }

    // Nouveau : Volontaire modifie le statut d'une tâche
    @PutMapping("/{taskId}/update-status")
    @PreAuthorize("hasRole('VOLUNTARY')")
    public ResponseEntity<TaskDTO> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam status newStatus,
            @AuthenticationPrincipal User volunteer) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, newStatus, volunteer));
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    // Nouveau : Responsable attribue une note à une tâche terminée
    @PutMapping("/{taskId}/assign-note")
    @PreAuthorize("hasRole('RESPONSIBLE')")
    public ResponseEntity<TaskDTO> assignTaskNote(
            @PathVariable Long taskId,
            @RequestParam int note,
            @AuthenticationPrincipal User responsible) {
        return ResponseEntity.ok(taskService.assignTaskNote(taskId, note, responsible));
    }

    // task par volontaire
    @GetMapping("/volunteer")
    @PreAuthorize("hasRole('VOLUNTARY')")
    public ResponseEntity<List<TaskDTO>> getTasksByVolunteer(@AuthenticationPrincipal User volunteer) {
        return ResponseEntity.ok(taskService.getTasksByVolunteer(volunteer));
    }
}