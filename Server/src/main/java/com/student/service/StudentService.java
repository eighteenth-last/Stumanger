package com.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.dto.StudentCreateRequest;
import com.student.dto.StudentResponse;
import com.student.dto.StudentUpdateRequest;
import com.student.entity.Student;
import com.student.entity.enums.Gender;

import java.util.List;

public interface StudentService {
    
    /**
     * 创建学生
     */
    StudentResponse createStudent(StudentCreateRequest request);
    
    /**
     * 更新学生信息
     */
    StudentResponse updateStudent(Long id, StudentUpdateRequest request);
    
    /**
     * 删除学生
     */
    void deleteStudent(Long id);
    
    /**
     * 根据ID获取学生详情
     */
    StudentResponse getStudentById(Long id);
    
    /**
     * 根据学号获取学生
     */
    StudentResponse getStudentByStudentNo(String studentNo);
    
    /**
     * 分页查询学生列表
     */
    IPage<StudentResponse> getStudentList(Integer page, Integer size, String studentNo, 
                                          String realName, Long classId, Gender gender);
    
    /**
     * 根据班级ID获取学生列表
     */
    List<StudentResponse> getStudentsByClassId(Long classId);
    
    /**
     * 批量分配班级
     */
    void batchAssignClass(List<Long> studentIds, Long classId);
    
    /**
     * 获取未分配班级的学生
     */
    List<StudentResponse> getStudentsWithoutClass();
    
    /**
     * 批量删除学生
     */
    void batchDeleteStudents(List<Long> ids);
}
