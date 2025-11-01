package com.student.entity;

import com.student.entity.enums.Gender;
import com.student.entity.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 实体类测试
 */
@SpringBootTest
public class EntityTest {

    @Test
    public void testUserEntity() {
        User user = new User("testuser", "password", "测试用户", UserRole.STUDENT);
        
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("测试用户", user.getRealName());
        assertEquals(UserRole.STUDENT, user.getRole());
        assertTrue(user.getStatus());
    }

    @Test
    public void testStudentEntity() {
        Student student = new Student(1L, "2024001", 1L, Gender.MALE);
        
        assertEquals(1L, student.getUserId());
        assertEquals("2024001", student.getStudentNo());
        assertEquals(1L, student.getClassId());
        assertEquals(Gender.MALE, student.getGender());
    }

    @Test
    public void testTeacherEntity() {
        Teacher teacher = new Teacher(1L, "T001", "计算机学院", "教授");
        
        assertEquals(1L, teacher.getUserId());
        assertEquals("T001", teacher.getTeacherNo());
        assertEquals("计算机学院", teacher.getDepartment());
        assertEquals("教授", teacher.getTitle());
    }

    @Test
    public void testClassEntity() {
        Class clazz = new Class("计算机科学与技术1班", "计算机科学与技术", "2024", 2024);
        
        assertEquals("计算机科学与技术1班", clazz.getClassName());
        assertEquals("计算机科学与技术", clazz.getMajor());
        assertEquals("2024", clazz.getGrade());
        assertEquals(2024, clazz.getEnrollYear());
    }

    @Test
    public void testCourseEntity() {
        Course course = new Course("Java程序设计", "CS101", new BigDecimal("3.0"), 1L);
        
        assertEquals("Java程序设计", course.getCourseName());
        assertEquals("CS101", course.getCourseCode());
        assertEquals(new BigDecimal("3.0"), course.getCredit());
        assertEquals(1L, course.getTeacherId());
    }

    @Test
    public void testScoreEntity() {
        Score score = new Score(1L, 1L, new BigDecimal("85.5"), "2024-1");
        
        assertEquals(1L, score.getStudentId());
        assertEquals(1L, score.getCourseId());
        assertEquals(new BigDecimal("85.5"), score.getScore());
        assertEquals("2024-1", score.getSemester());
    }

    @Test
    public void testEnums() {
        assertEquals("admin", UserRole.ADMIN.getCode());
        assertEquals("管理员", UserRole.ADMIN.getDescription());
        
        assertEquals(0, Gender.FEMALE.getCode());
        assertEquals("女", Gender.FEMALE.getDescription());
        assertEquals(1, Gender.MALE.getCode());
        assertEquals("男", Gender.MALE.getDescription());
    }
}