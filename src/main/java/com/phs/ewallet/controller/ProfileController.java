package com.phs.ewallet.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.phs.ewallet.dto.AuthDTO;
import com.phs.ewallet.dto.ProfileDTO;
import com.phs.ewallet.service.ProfileService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<?> registerProfile(@RequestBody ProfileDTO dto) {
        try {
            ProfileDTO registeredProfile = profileService.registerProfile(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Email already exists"));
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }

    /*@GetMapping("/active")
    public ResponseEntity<String> activeProfile(@RequestParam String token) {
        boolean isActivated = profileService.activeProfile(token);
        if(isActivated) {
            return ResponseEntity.ok("Tài khoản được kích hoạt thành công!");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token kích hoạt không tìm thấy hoặc đã được sử dụng");
        }
    }*/

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");
            
            // Handle both "username" and "email" field names for flexibility
            if (email == null) {
                email = credentials.get("username");
            }
            
            if (email == null || password == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "message", "Email and password are required"
                        ));
            }
            
            AuthDTO authDTO = new AuthDTO(email, password);
            
            // check account active
            if (!profileService.isAccountActive(authDTO.getEmail())) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "message", "Account is not active. Please try again."
                        ));
            }

            // authenticate & generate token
            Map<String, Object> response =
                    profileService.authenticateAndGenerateToken(authDTO);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Test OK";
    }
}
