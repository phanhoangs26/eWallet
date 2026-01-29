package com.phs.ewallet.service;

import com.phs.ewallet.entity.Profile;
import com.phs.ewallet.repository.ProfileRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final ProfileRepo profileRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Profile exsProfile = profileRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found with email: " + email));
    return User.builder()
            .username(exsProfile.getEmail())
            .password(exsProfile.getPassword())
            .authorities(Collections.emptyList())
            .build();
    }
}
