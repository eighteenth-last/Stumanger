package com.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程实体类
 */
@TableName("course")
public class Course {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "课程名称不能为空")
    @Size(max = 50, message = "课程名称长度不能超过50个字符")
    @TableField("course_name")
    private String courseName;

    @NotBlank(message = "课程代码不能为空")
    @Size(max = 20, message = "课程代码长度不能超过20个字符")
    @TableField("course_code")
    private String courseCode;

    @NotNull(message = "学分不能为空")
    @DecimalMin(value = "0.1", message = "学分必须大于0")
    @TableField("credit")
    private BigDecimal credit;

    @TableField("hours")
    private Integer hours;

    @TableField("semester")
    private String semester;

    @TableField("classroom")
    private String classroom;

    @TableField("class_time")
    private String classTime;

    @NotNull(message = "授课教师不能为空")
    @TableField("teacher_id")
    private Long teacherId;

    @TableField("description")
    private String description;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 关联教师信息（非数据库字段）
    @TableField(exist = false)
    private Teacher teacher;

    // 课程成绩列表（非数据库字段）
    @TableField(exist = false)
    private List<Score> scores;

    // 构造函数
    public Course() {}

    public Course(String courseName, String courseCode, BigDecimal credit, Long teacherId) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.credit = credit;
        this.teacherId = teacherId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", courseName='" + courseName + '\'' +
                ", courseCode='" + courseCode + '\'' +
                ", credit=" + credit +
                ", hours=" + hours +
                ", semester='" + semester + '\'' +
                ", classroom='" + classroom + '\'' +
                ", classTime='" + classTime + '\'' +
                ", teacherId=" + teacherId +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}