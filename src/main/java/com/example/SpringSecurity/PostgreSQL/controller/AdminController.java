package com.example.SpringSecurity.PostgreSQL.controller;

import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ApiResponse;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.UserResponse;
import com.example.SpringSecurity.PostgreSQL.service.AdminService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    public AdminController(AdminService adminService) {
        this.adminService= adminService;
    }

    @GetMapping("allusers")  //allusers?page=0&size=10
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(Pageable pageable) {
        List<UserResponse> users = adminService.findAll(pageable).getContent();
        ApiResponse<List<UserResponse>> response = ApiResponse.success(users);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete-user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id){
        adminService.deleteUser(id);
        ApiResponse<String> response = ApiResponse.success("Usuario Deletado com sucesso");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/disable-user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> disableUser(@PathVariable Long id) {
        adminService.disableUser(id);
        ApiResponse<String> response = ApiResponse.success("Usuario desabilitado com sucesso");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
