package tn.ucar.enicar.info.projetspring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.ucar.enicar.info.projetspring.controllers.TeamController;
import tn.ucar.enicar.info.projetspring.entities.Team;
import tn.ucar.enicar.info.projetspring.entities.TeamDTO;
import tn.ucar.enicar.info.projetspring.sevices.TeamService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TeamTest {

    private MockMvc mockMvc;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    private ObjectMapper objectMapper;

    private Team team;
    private TeamDTO teamDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        team = new Team();
        team.setName("Équipe Logistique");
        team.setDescription("Responsable de la logistique");

        teamDTO = new TeamDTO();
        teamDTO.setId(1L);
        teamDTO.setName("Équipe Logistique");
        teamDTO.setDescription("Responsable de la logistique");
        teamDTO.setEventId(5L);

        mockMvc = MockMvcBuilders.standaloneSetup(teamController)
                .build();
    }

    @Test
    void createTeam_Success_ReturnsTeamDTO() throws Exception {
        // Arrange
        when(teamService.createTeam(any(Team.class), eq(5L))).thenReturn(teamDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/team")
                        .param("eventId", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(team))
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Équipe Logistique"));
    }

    @Test
    void createTeam_Unauthorized_ReturnsForbidden() throws Exception {
        // Arrange
        when(teamService.createTeam(any(Team.class), eq(5L))).thenReturn(teamDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/team")
                        .param("eventId", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(team)));
    }
}