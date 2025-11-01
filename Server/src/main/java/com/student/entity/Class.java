package com.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 班级实体类
 */
@TableName("class")
public class Class {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "班级名称不能为空")
    @Size(max = 50, message = "班级名称长度不能超过50个字符")
    @TableField("class_name")
    private String className;

    @Size(max = 50, message = "专业长度不能超过50个字符")
    @TableField("major")
    private String major;

    @Size(max = 10, message = "年级长度不能超过10个字符")
    @TableField("grade")
    private String grade;

    @TableField("enroll_year")
    private Integer enrollYear;

    @TableField("class_teacher_id")
    private Long classTeacherId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 关联班主任信息（非数据库字段）
    @TableField(exist = false)
    private Teacher classTeacher;

    // 班级学生列表（非数据库字段）
    @TableField(exist = false)
    private List<Student> students;

    // 构造函数
    public Class() {}

    public Class(String className, String major, String grade, Integer enrollYear) {
        this.className = className;
        this.major = major;
        this.grade = grade;
        this.enrollYear = enrollYear;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Teacher getClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(Teacher classTeacher) {
        this.classTeacher = classTeacher;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    @Override
    public String toString() {
        return "Class{" +
                "id=" + id +
                ", className='" + className + '\'' +
                ", major='" + major + '\'' +
                ", grade='" + grade + '\'' +
                ", enrollYear=" + enrollYear +
                ", classTeacherId=" + classTeacherId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}