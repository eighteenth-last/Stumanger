package com.student.service;

import com.student.dto.ScoreCreateRequest;
import com.student.dto.StudentCreateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ExcelService {
    
    /**
     * 导入学生数据
     */
    void importStudents(MultipartFile file) throws IOException;
    
    /**
     * 导出学生数据
     */
    byte[] exportStudents(Long classId, String studentNo, String realName) throws IOException;
    
    /**
     * 导入成绩数据
     */
    void importScores(MultipartFile file) throws IOException;
    
    /**
     * 导出成绩数据
     */
    byte[] exportScores(Long studentId, Long courseId, String semester) throws IOException;
    
    /**
     * 导出学生模板
     */
    byte[] exportStudentTemplate() throws IOException;
    
    /**
     * 导出成绩模板
     */
    byte[] exportScoreTemplate() throws IOException;
}
