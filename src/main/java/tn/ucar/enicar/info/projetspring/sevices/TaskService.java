    package tn.ucar.enicar.info.projetspring.sevices;

    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
    import tn.ucar.enicar.info.projetspring.entities.*;
    import tn.ucar.enicar.info.projetspring.repositories.EventRepository;
    import tn.ucar.enicar.info.projetspring.repositories.TaskRepository;
    import tn.ucar.enicar.info.projetspring.repositories.TeamRepository;
    import tn.ucar.enicar.info.projetspring.repositories.userRepo;

    import java.util.HashSet;
    import java.util.List;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    public class TaskService {
        private final TaskRepository taskRepository;
        private final EventRepository eventRepository;
        private final TeamRepository teamRepository;
        private final userRepo userRepository;

        public TaskDTO createTask(TaskDTO taskDTO, User responsible) {
            if (!responsible.getRole().equals(Role.RESPONSIBLE)) {
                throw new IllegalStateException("Only RESPONSIBLE users can create tasks");
            }

            event event = taskDTO.getEventId() != null
                    ? eventRepository.findById(taskDTO.getEventId())
                    .orElseThrow(() -> new IllegalArgumentException("Event not found"))
                    : null;

            Team team = taskDTO.getTeamId() != null
                    ? teamRepository.findById(taskDTO.getTeamId())
                    .orElseThrow(() -> new IllegalArgumentException("Team not found"))
                    : null;

            task newTask = task.builder()
                    .title(taskDTO.getTitle())
                    .description(taskDTO.getDescription())
                    .deadline(taskDTO.getDeadline())
                    .Status(taskDTO.getStatus() != null ? taskDTO.getStatus() : status.ToDo)
                    .note(taskDTO.getNote())
                    .responsible(responsible)
                    .event(event)
                    .team(team)
                    .volunteers(new HashSet<>())
                    .build();

            task savedTask = taskRepository.save(newTask);
            return mapToDTO(savedTask);
        }
        public List<TaskDTO> getAllTasks() {
            return taskRepository.findAll()
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }
        public List<TaskDTO> getTasksByVolunteer(User volunteer) {
            if (!volunteer.getRole().equals(Role.VOLUNTARY)) {
                throw new IllegalStateException("Only VOLUNTARY users can access their tasks");
            }
            return taskRepository.findByVolunteersContaining(volunteer)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }

        public List<TaskDTO> getTasksByResponsible(User responsible) {
            return taskRepository.findByResponsible(responsible)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }

        public TaskDTO updateTaskStatus(Long taskId, status newStatus, User volunteer) {
            task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found"));

            if (!task.getVolunteers().contains(volunteer)) {
                throw new IllegalStateException("User is not a volunteer for this task");
            }

            task.setStatus(newStatus);
            task updatedTask = taskRepository.save(task);
            return mapToDTO(updatedTask);
        }

        public TaskDTO assignTaskNote(Long taskId, int note, User responsible) {
            task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found"));

            if (!task.getResponsible().getId().equals(responsible.getId())) {
                throw new IllegalStateException("User is not the responsible for this task");
            }

            if (task.getStatus() != status.Done) {
                throw new IllegalStateException("Task must be in Done status to assign a note");
            }

            if (note < 0 || note > 100) {
                throw new IllegalArgumentException("Note must be between 0 and 100");
            }

            task.setNote(note);
            task updatedTask = taskRepository.save(task);
            return mapToDTO(updatedTask);
        }

        private TaskDTO mapToDTO(task task) {
            String volunteerName = "No volunteer assigned";
            if (!task.getVolunteers().isEmpty()) {
                User firstVolunteer = task.getVolunteers().iterator().next();
                volunteerName = firstVolunteer.getUsername(); // Assuming username is email
            }

            return TaskDTO.builder()
                    .id(task.getId())
                    .title(task.getTitle())
                    .description(task.getDescription())
                    .deadline(task.getDeadline())
                    .Status(task.getStatus())
                    .note(task.getNote() != null ? task.getNote() : null)
                    .createdAt(task.getCreatedAt())
                    .completedAt(task.getCompletedAt())
                    .eventId(task.getEvent() != null ? task.getEvent().getId() : null)
                    .teamId(task.getTeam() != null ? task.getTeam().getId() : null)
                    .responsibleId(task.getResponsible() != null ? task.getResponsible().getId() : null)
                    .responsibleName(task.getResponsible() != null ? task.getResponsible().getUsername() : null)
                    .volunteerName(volunteerName)
                    .volunteerIds(task.getVolunteers() != null
                            ? task.getVolunteers().stream().map(User::getId).collect(Collectors.toSet())
                            : new HashSet<>())
                    .build();
        }
    }