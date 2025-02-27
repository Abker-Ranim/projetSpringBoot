package tn.ucar.enicar.info.projetspring.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.ucar.enicar.info.projetspring.entities.User;
import tn.ucar.enicar.info.projetspring.sevices.userService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class userController {

    userService userServ;
    //http://localhost:8089/SpringMVC/user/retrieve-all-users
    @GetMapping("/retrieve-all-users")
    public List<User> getUsers() {
        List<User> listUsers = userServ.retrieveAllUsers();
        return listUsers;
    }
}
