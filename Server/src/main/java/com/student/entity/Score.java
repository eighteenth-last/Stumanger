package com.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩实体类
 */
@TableName("score")
public class Score {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotNull(message = "学生ID不能为空")
    @TableField("student_id")
    private Long studentId;

    @NotNull(message = "课程ID不能为空")
    @TableField("course_id")
    private Long courseId;

    @NotNull(message = "成绩不能为空")
    @DecimalMin(value = "0", message = "成绩不能小于0")
    @DecimalMax(value = "100", message = "成绩不能大于100")
    @TableField("score")
    private BigDecimal score;

    @NotBlank(message = "学期不能为空")
    @Size(max = 20, message = "学期长度不能超过20个字符")
    @TableField("semester")
    private String semester;

    @TableField("exam_type")
    private String examType;

    @TableField("exam_date")
    private java.time.LocalDate examDate;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 关联学生信息（非数据库字段）
    @TableField(exist = false)
    private Student student;

    // 关联课程信息（非数据库字段）
    @TableField(exist = false)
    private Course course;

    // 构造函数
    public Score() {}

    public Score(Long studentId, Long courseId, BigDecimal score, String semester) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.score = score;
        this.semester = semester;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public java.time.LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(java.time.LocalDate examDate) {
        this.examDate = examDate;
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

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", score=" + score +
                ", semester='" + semester + '\'' +
                ", examType='" + examType + '\'' +
                ", examDate=" + examDate +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}