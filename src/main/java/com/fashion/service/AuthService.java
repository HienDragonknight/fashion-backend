package com.fashion.service;

import com.fashion.dto.request.LoginRequest;
import com.fashion.dto.request.RegisterRequest;
import com.fashion.dto.response.AuthResponse;
import com.fashion.dto.response.UserResponse;
import com.fashion.entity.PasswordResetToken;
import com.fashion.entity.User;
import com.fashion.exception.BusinessException;
import com.fashion.exception.ResourceNotFoundException;
import com.fashion.repository.PasswordResetTokenRepository;
import com.fashion.repository.UserRepository;
import com.fashion.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request.getEmail() == null && request.getPhone() == null) {
            throw new BusinessException("Phải cung cấp email hoặc số điện thoại");
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email đã tồn tại");
        }
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("Số điện thoại đã tồn tại");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_CUSTOMER")
                .build();

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(
                user.getEmail() != null ? user.getEmail() : user.getPhone());

        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(toUserResponse(user))
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword())
        );

        User user = userRepository.findByEmailOrPhone(request.getIdentifier())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tìm thấy"));

        if (!user.getIsActive()) {
            throw new BusinessException("Tài khoản đã bị khóa");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getIdentifier());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(toUserResponse(user))
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByEmailOrPhone(username)
                .orElseThrow(() -> new BusinessException("Refresh token không hợp lệ"));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new BusinessException("Refresh token không hợp lệ hoặc đã hết hạn");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(toUserResponse(user))
                .build();
    }

    @Transactional
    public void logout(String username) {
        userRepository.findByEmailOrPhone(username).ifPresent(user -> {
            user.setRefreshToken(null);
            userRepository.save(user);
        });
    }

    @Transactional
    public void forgotPassword(String identifier) {
        User user = userRepository.findByEmailOrPhone(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusHours(2))
                .build();
        resetTokenRepository.save(resetToken);

        if (user.getEmail() != null) {
            emailService.sendPasswordReset(user.getEmail(), token);
        }
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Token không hợp lệ"));

        if (resetToken.getUsed() || resetToken.isExpired()) {
            throw new BusinessException("Token đã hết hạn hoặc đã được sử dụng");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
