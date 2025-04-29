package tn.ucar.enicar.info.projetspring.sevices;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.ucar.enicar.info.projetspring.entities.*;
import tn.ucar.enicar.info.projetspring.repositories.TaskRepository;
import tn.ucar.enicar.info.projetspring.repositories.userRepo;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final userRepo userRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public Integer calculateUserScore(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() != Role.VOLUNTARY) {
            return 0;
        }

        List<task> tasks = taskRepository.findByVolunteersContaining(user);
        int score = tasks.stream()
                .filter(task -> task.getNote() != null && task.getNote() > 0)
                .mapToInt(task::getNote)
                .sum();

        user.setScore(score);
        userRepository.save(user);
        return score;
    }

    @Transactional
    public void calculateAllVolunteerScores() {
        List<User> volunteers = userRepository.findByRole(Role.VOLUNTARY);
        for (User volunteer : volunteers) {
            calculateUserScore(volunteer.getId());
        }
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryDTO> getLeaderboard() {
        List<User> volunteers = userRepository.findByRole(Role.VOLUNTARY);
        List<LeaderboardEntryDTO> leaderboard = volunteers.stream()
                .sorted(Comparator.comparingInt(user -> user.getScore() != null ? -user.getScore() : 0))
                .map(user -> LeaderboardEntryDTO.builder()
                        .email(user.getEmail())
                        .score(user.getScore() != null ? user.getScore() : 0)
                        .rank(0)
                        .build())
                .collect(Collectors.toList());

        for (int i = 0; i < leaderboard.size(); i++) {
            int rank;
            if (i == 0) {
                rank = 1;
            } else {
                Integer currentScore = leaderboard.get(i).getScore();
                Integer prevScore = leaderboard.get(i - 1).getScore();
                rank = currentScore.equals(prevScore) ? leaderboard.get(i - 1).getRank() : i + 1;
            }
            leaderboard.get(i).setRank(rank);
        }

        return leaderboard;
    }
}