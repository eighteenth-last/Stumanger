package com.student.controller;

import com.student.common.result.Result;
import com.student.dto.LoginRequest;
import com.student.dto.LoginResponse;
import com.student.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证控制器
 */
@Tag(name = "用户认证", description = "用户登录、注销、令牌刷新等认证相关接口")
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录", description = "使用用户名和密码进行登录，返回JWT令牌")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登录成功", 
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "用户名或密码错误"),
        @ApiResponse(responseCode = "401", description = "账号已被禁用")
    })
    @PostMapping("/login")
    public Result<LoginResponse> login(
            @Parameter(description = "登录请求信息", required = true)
            @Validated @RequestBody LoginRequest loginRequest) {
        logger.info("用户登录请求: {}", loginRequest.getUsername());
        
        LoginResponse response = authService.login(loginRequest);
        return Result.success("登录成功", response);
    }

    /**
     * 用户注销
     */
    @Operation(summary = "用户注销", description = "注销当前登录用户，使JWT令牌失效")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "注销成功"),
        @ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @PostMapping("/logout")
    public Result<Void> logout(
            @Parameter(hidden = true) HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token != null) {
            authService.logout(token);
        }
        return Result.success("注销成功", null);
    }

    /**
     * 刷新令牌
     */
    @Operation(summary = "刷新令牌", description = "使用当前JWT令牌刷新获取新的令牌")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "令牌刷新成功",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "令牌不能为空"),
        @ApiResponse(responseCode = "401", description = "令牌无效或已过期")
    })
    @PostMapping("/refresh")
    public Result<LoginResponse> refreshToken(
            @Parameter(hidden = true) HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null) {
            return Result.error(400, "令牌不能为空");
        }
        
        LoginResponse response = authService.refreshToken(token);
        return Result.success("令牌刷新成功", response);
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的基本信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/me")
    public Result<Object> getCurrentUser(
            @Parameter(hidden = true) HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        String role = (String) request.getAttribute("role");
        
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }
        
        return Result.success("获取用户信息成功", new Object() {
            public final Long userId = (Long) request.getAttribute("userId");
            public final String username = (String) request.getAttribute("username");
            public final String role = (String) request.getAttribute("role");
        });
    }

    /**
     * 从请求中获取JWT令牌
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}