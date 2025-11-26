package com.example.SpringSecurity.PostgreSQL.controller;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.UpdateUserRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.UpdateUserResponse;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UpdateUserResponse> getUserProfile() {
        User user = userService.findUser();
        return ResponseEntity.status(HttpStatus.OK).body(new UpdateUserResponse(user.getName(),user.getEmail()));
    }
    @PutMapping("/profile/update")
    public ResponseEntity<UpdateUserResponse> updateUserProfile(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        User updatedUser = userService.updateUser(updateUserRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new UpdateUserResponse(updatedUser.getName(),updatedUser.getEmail()));
    }
    @DeleteMapping("/profile/delete")
    public ResponseEntity<Void> deleteUserProfile() {
        userService.deleteUser();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
