package com.bash.login_sec.service;

import com.bash.login_sec.dto.LoginUserDto;
import com.bash.login_sec.dto.RegisterUserDto;
import com.bash.login_sec.model.User;
import com.bash.login_sec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;

    public User signUp(RegisterUserDto input){
        User user = new User(input.getUsername(), input.getEmail(), passwordEncoder.encode(input.getPassword()));
        user.setVerificantionCode(generateVerificationCode());
        user.setVerificationExpiration(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        return userRepository.save(user);

    }
    public User authenticate(LoginUserDto input){
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(()->new UsernameNotFoundException("User not found"));
        if(!user.isEnabled()){
            throw new RuntimeException("Account is not verified. Please verify your account");
        }


    }

}
