package com.cricmate.CricketRecordsApplication.service.impl;

import com.cricmate.CricketRecordsApplication.Entity.Access;
import com.cricmate.CricketRecordsApplication.Entity.User;
import com.cricmate.CricketRecordsApplication.Repository.UserRepository;
import com.cricmate.CricketRecordsApplication.dao.Request.SignInRequest;
import com.cricmate.CricketRecordsApplication.dao.Request.SignUpRequest;
import com.cricmate.CricketRecordsApplication.dao.Response.JwtAuthenticationResponse;
import com.cricmate.CricketRecordsApplication.service.AuthenticationService;
import com.cricmate.CricketRecordsApplication.service.jwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.Subject;
import java.util.Collection;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final jwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Override
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        var user = User.builder().firstName(request.getFirstName()).lastName(request.getLastName())
                .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                .BattingStyle(request.getBattingStyle()).PlayingRole(request.getPlayingRole())
                .BowlingStyle(request.getBowlingStyle())
                .Gender(request.getGender()).phoneNumber(request.getPhoneNumber())
                .accessType(Access.USER).build();
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        JwtAuthenticationResponse response = JwtAuthenticationResponse.builder()
                .token(jwt).user(user)
                .build();

        return response;
    }

    @Override
    public JwtAuthenticationResponse signin(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("Invalid email/password"));
        var jwt = jwtService.generateToken(user);
        JwtAuthenticationResponse response = JwtAuthenticationResponse.builder()
                .token(jwt).user(user)
                .build();

        return response;
    }
}

