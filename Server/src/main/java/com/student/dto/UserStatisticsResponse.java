package com.student.dto;

import com.student.entity.enums.UserRole;
import java.util.Map;

/**
 * 用户统计响应DTO
 */
public class UserStatisticsResponse {
    
    private Long totalUsers;
    private Long activeUsers;
    private Long inactiveUsers;
    private Map<UserRole, Long> usersByRole;
    
    // 构造函数
    public UserStatisticsResponse() {}
    
    public UserStatisticsResponse(Long totalUsers, Long activeUsers, Long inactiveUsers, Map<UserRole, Long> usersByRole) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.inactiveUsers = inactiveUsers;
        this.usersByRole = usersByRole;
    }
    
    // Getters and Setters
    public Long getTotalUsers() {
        return totalUsers;
    }
    
    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }
    
    public Long getActiveUsers() {
        return activeUsers;
    }
    
    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
    }
    
    public Long getInactiveUsers() {
        return inactiveUsers;
    }
    
    public void setInactiveUsers(Long inactiveUsers) {
        this.inactiveUsers = inactiveUsers;
    }
    
    public Map<UserRole, Long> getUsersByRole() {
        return usersByRole;
    }
    
    public void setUsersByRole(Map<UserRole, Long> usersByRole) {
        this.usersByRole = usersByRole;
    }
    
    @Override
    public String toString() {
        return "UserStatisticsResponse{" +
                "totalUsers=" + totalUsers +
                ", activeUsers=" + activeUsers +
                ", inactiveUsers=" + inactiveUsers +
                ", usersByRole=" + usersByRole +
                '}';
    }
}