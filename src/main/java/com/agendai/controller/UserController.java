package com.agendai.controller;

import com.agendai.dto.UserDTO;
import com.agendai.model.User;
import com.agendai.repository.UserRepository;
import com.agendai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public User createUser(@RequestBody UserDTO dto) {
        return userService.register(dto);
    }

    @GetMapping
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody UserDTO dto){
        return userService.updateUser(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);

    }
}
