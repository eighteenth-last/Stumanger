package com.student.dto;

import jakarta.validation.constraints.Size;

public class ClassUpdateRequest {
    
    @Size(max = 50, message = "班级名称长度不能超过50个字符")
    private String className;
    
    @Size(max = 50, message = "专业长度不能超过50个字符")
    private String major;
    
    @Size(max = 10, message = "年级长度不能超过10个字符")
    private String grade;
    
    private Integer enrollYear;
    
    private Long classTeacherId;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Integer getEnrollYear() {
        return enrollYear;
    }

    public void setEnrollYear(Integer enrollYear) {
        this.enrollYear = enrollYear;
    }

    public Long getClassTeacherId() {
        return classTeacherId;
    }

    public void setClassTeacherId(Long classTeacherId) {
        this.classTeacherId = classTeacherId;
    }
}
