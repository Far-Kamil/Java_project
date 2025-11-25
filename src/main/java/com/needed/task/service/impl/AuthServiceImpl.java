package com.needed.task.service.impl;


import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.needed.task.dto.LoginRequest;
import com.needed.task.dto.LoginResponse;
import com.needed.task.dto.UserLoggedDto;
import com.needed.task.exception.AppException;
import com.needed.task.jwt.JwtTokenProviderImpl;
import com.needed.task.mapper.UserMapper;
import com.needed.task.model.Token;
import com.needed.task.model.User;
import com.needed.task.repository.TokenRepository;
import com.needed.task.repository.UserRepository;
import com.needed.task.service.AuthService;
import com.needed.task.util.CookieUtil;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Value("${JWT_ACCESS_TOKEN_DURATION_MINUTE}")
    private long accessTokenDurationMinute;
    @Value("${JWT_ACCESS_TOKEN_DURATION_SECOND}")
    private long accessTokenDurationSecond;
    @Value("${JWT_REFRESH_TOKEN_DURATION_DAY}")
    private long refreshTokenDurationDay;
    @Value("${JWT_REFRESH_TOKEN_DURATION_SECOND}")
    private long refreshTokenDurationSecond;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenProviderImpl tokenProvider;
    private final CookieUtil cookieUtil;
    private final AuthenticationManager authenticationManager;
    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest, 
        String accessToken, String refreshToken) {
        try {
            // Аутентификация
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.username(),
                    loginRequest.password()
                )
            );
            
            // Получаем пользователя
            User user = (User) authentication.getPrincipal();
            
            // Создаем claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", user.getRole().getAuthority());
            claims.put("username", user.getUsername());
            
            // Генерируем токены
            var accessTokenObj = tokenProvider.generateAccessToken(
                claims, 15, ChronoUnit.MINUTES, user
            );
            var refreshTokenObj = tokenProvider.generateRefreshToken(
                7, ChronoUnit.DAYS, user
            );
            
            // Устанавливаем cookies
            ResponseCookie accessCookie = ResponseCookie.from("access_token", accessTokenObj.getValue())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(15 * 60)
                .build();
                
            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshTokenObj.getValue())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();
            
            // Устанавливаем аутентификацию в контекст
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new LoginResponse(true, user.getRole().getAuthority()));
                
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest()
                .body(new LoginResponse(false, ""));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new LoginResponse(false, ""));
        }
    }
    @Override
    public ResponseEntity<LoginResponse> refresh(String refreshToken) {
        boolean refreshTokenValid = tokenProvider.validateToken(refreshToken);

        if(!refreshTokenValid)
            throw new AppException(HttpStatus.BAD_REQUEST, "Refresh token is invalid");

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, "User not found")
        );

        Token newAccessToken = tokenProvider.generateAccessToken(
                Map.of("role", user.getRole().getAuthority()),
                accessTokenDurationMinute,
                ChronoUnit.MINUTES,
                user
        );

        HttpHeaders responseHeaders = new HttpHeaders();
        addAccessTokenCookie(responseHeaders, newAccessToken);

        LoginResponse loginResponse = new LoginResponse(true, user.getRole().getName());

        return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
    }
    @Override
    public ResponseEntity<LoginResponse> logout(String accessToken, String refreshToken) {
         ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0)
            .build();
            
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0)
            .build();
            
        SecurityContextHolder.clearContext();
        
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(new LoginResponse(false, ""));
    }
    @Override
    public UserLoggedDto getUserLoggedInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof AnonymousAuthenticationToken)
            throw new AppException(HttpStatus.UNAUTHORIZED, "No user authenticated");

        String username = authentication.getName();

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, "User not found")
        );

        return UserMapper.userToUserLoggedDto(user);
    }
    private void addAccessTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(token.getValue(), accessTokenDurationSecond).toString());
    }
    private void addRefreshTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(token.getValue(), refreshTokenDurationSecond).toString());
    }
    
}
