package tn.ucar.enicar.info.projetspring.sevices;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tn.ucar.enicar.info.projetspring.entities.*;
import tn.ucar.enicar.info.projetspring.repositories.TaskRepository;
import tn.ucar.enicar.info.projetspring.repositories.userRepo;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final userRepo userRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public Integer calculateUserScore(Integer userId) {
        logger.info("Starting score calculation for user ID: {}", userId);

        // Fetch the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        logger.info("User found: {} with role: {}", user.getEmail(), user.getRole());

        // Check if user is VOLUNTARY
        if (user.getRole() != Role.VOLUNTARY) {
            logger.warn("User {} is not a VOLUNTARY (role: {}), returning score 0", user.getEmail(), user.getRole());
            return 0;
        }

        // Get all tasks where the user is a volunteer
        List<task> tasks = taskRepository.findByVolunteersContaining(user);
        logger.info("Found {} tasks for user {}", tasks.size(), user.getEmail());

        // Log task details
        tasks.forEach(task -> logger.info("Task ID: {}, Title: {}, Note: {}", task.getId(), task.getTitle(), task.getNote() == null ? "NULL" : task.getNote()));

        // Calculate score as sum of task notes
        int score = tasks.stream()
                .filter(task -> task.getNote() != null && task.getNote() > 0) // Skip null or zero notes
                .mapToInt(task::getNote)
                .peek(note -> logger.info("Adding note: {}", note))
                .sum();

        logger.info("Calculated score for user {}: {}", user.getEmail(), score);

        // Update user's score
        user.setScore(score);
        User savedUser = userRepository.save(user);
        logger.info("Saved user {} with score: {}", savedUser.getEmail(), savedUser.getScore());

        return score;
    }
    @Transactional
    public void calculateAllVolunteerScores() {
        logger.info("Starting score calculation for all VOLUNTARY users");

        // Fetch all VOLUNTARY users
        List<User> volunteers = userRepository.findByRole(Role.VOLUNTARY);
        logger.info("Found {} VOLUNTARY users", volunteers.size());

        // Calculate score for each volunteer
        for (User volunteer : volunteers) {
            logger.info("Processing user: {}", volunteer.getEmail());
            calculateUserScore(volunteer.getId());
        }

        logger.info("Completed score calculation for all VOLUNTARY users");
    }



    @Transactional(readOnly = true)
    public List<LeaderboardEntryDTO> getLeaderboard() {
        logger.info("Generating leaderboard for all VOLUNTARY users");

        // Fetch all VOLUNTARY users
        List<User> volunteers = userRepository.findByRole(Role.VOLUNTARY);
        logger.info("Found {} VOLUNTARY users", volunteers.size());

        // Create DTOs without ranks
        List<LeaderboardEntryDTO> leaderboard = volunteers.stream()
                .sorted(Comparator.comparingInt(user -> user.getScore() != null ? -user.getScore() : 0))
                .map(user -> LeaderboardEntryDTO.builder()
                        .email(user.getEmail())
                        .score(user.getScore() != null ? user.getScore() : 0)
                        .rank(0) // Temporary rank
                        .build())
                .collect(Collectors.toList());

        // Assign ranks, handling ties
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
            logger.info("User {}: score={}, rank={}", leaderboard.get(i).getEmail(), leaderboard.get(i).getScore(), rank);
        }

        logger.info("Leaderboard generated with {} entries", leaderboard.size());
        return leaderboard;
    }
}