package com.phs.ewallet.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.phs.ewallet.dto.AuthDTO;
import com.phs.ewallet.dto.ProfileDTO;
import com.phs.ewallet.entity.Profile;
import com.phs.ewallet.repository.ProfileRepo;
import com.phs.ewallet.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepo profileRepo;
    //private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${app.activation.url}")
    private String activationURL;

    public ProfileDTO registerProfile(ProfileDTO dto) {
        Profile newProfile = toEntity(dto);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile.setIsActive(true); //đăng kí luôn luôn là active chưa cần authenticate vì chưa sử dụng dc email
        newProfile = profileRepo.save(newProfile);
        //Send activation email
        /*String activationLink = activationURL + "api/v1/active?token=" + newProfile.getActivationToken();
        String subject = "Kích hoạt tài khoản eWallet";
        String body = "Nhấn vào liên kết để kích hoạt tài khoản: " + activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, body);*/

        // Set isActive trong DTO trước khi return
        ProfileDTO result = toDTO(newProfile);
        result.setIsActive(true);
        return result;
    }

    public boolean isAccountActive(String email) {
        return profileRepo.findByEmail(email)
                .map(Profile::getIsActive)
                .orElse(false);
    }

    public Profile getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("User is not authenticated");
        }

        return profileRepo.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Profile not found with email: " + authentication.getName()
                        )
                );
    }

    public ProfileDTO getPublicProfile(String email) {
        Profile currentProfile;
        if (email == null) {
            currentProfile = getCurrentProfile();
        } else {
            currentProfile = profileRepo.findByEmail(email)
                    .orElseThrow(() ->
                            new RuntimeException("Profile not found with email: " + email));
        }

        return ProfileDTO.builder()
                .id(currentProfile.getId())
                .fullName(currentProfile.getFullName())
                .email(currentProfile.getEmail())
                .profileImageUrl(currentProfile.getProfileImageUrl())
                .createdAt(currentProfile.getCreatedAt())
                .updatedAt(currentProfile.getUpdatedAt())
                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
            //Generate JWT token
            String token = jwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                    "token", token,
                    "user", getPublicProfile(authDTO.getEmail())
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password");
        }
    }

    /*public boolean activateProfile(String activationToken) {
        return profileRepo.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepo.save(profile);
                    return true;
                })
                .orElse(false);
    }*/

    public Profile toEntity(ProfileDTO dto) {
        return Profile.builder()
                .id(dto.getId())
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .profileImageUrl(dto.getProfileImageUrl())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
    }

    private ProfileDTO toDTO(Profile p) {
        return ProfileDTO.builder()
                .id(p.getId())
                .fullName(p.getFullName())
                .email(p.getEmail())
                .profileImageUrl(p.getProfileImageUrl())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .isActive(p.getIsActive())
                .build();
    }
}
