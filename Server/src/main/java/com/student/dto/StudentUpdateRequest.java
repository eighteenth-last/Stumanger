package com.student.dto;

import com.student.entity.enums.Gender;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class StudentUpdateRequest {
    
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;
    
    private Long classId;
    
    private Gender gender;
    
    private LocalDate birthDate;
    
    private String email;
    
    private String phone;
    
    @Size(max = 255, message = "地址长度不能超过255个字符")
    private String address;
    
    private Boolean status;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
