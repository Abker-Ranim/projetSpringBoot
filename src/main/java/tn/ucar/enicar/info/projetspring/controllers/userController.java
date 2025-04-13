package tn.ucar.enicar.info.projetspring.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.ucar.enicar.info.projetspring.entities.User;
import tn.ucar.enicar.info.projetspring.entities.UserScoreDTO;
import tn.ucar.enicar.info.projetspring.sevices.UserService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class userController {

}
