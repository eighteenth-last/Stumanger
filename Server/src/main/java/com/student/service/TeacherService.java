package com.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.dto.TeacherCreateRequest;
import com.student.dto.TeacherResponse;
import com.student.dto.TeacherUpdateRequest;

import java.util.List;

public interface TeacherService {
    
    /**
     * 创建教师
     */
    TeacherResponse createTeacher(TeacherCreateRequest request);
    
    /**
     * 更新教师信息
     */
    TeacherResponse updateTeacher(Long id, TeacherUpdateRequest request);
    
    /**
     * 删除教师
     */
    void deleteTeacher(Long id);
    
    /**
     * 根据ID获取教师详情
     */
    TeacherResponse getTeacherById(Long id);
    
    /**
     * 根据教师编号获取教师
     */
    TeacherResponse getTeacherByTeacherNo(String teacherNo);
    
    /**
     * 分页查询教师列表
     */
    IPage<TeacherResponse> getTeacherList(Integer page, Integer size, String teacherNo,
                                          String realName, String department, String title);
    
    /**
     * 根据部门获取教师列表
     */
    List<TeacherResponse> getTeachersByDepartment(String department);
    
    /**
     * 根据职称获取教师列表
     */
    List<TeacherResponse> getTeachersByTitle(String title);
    
    /**
     * 获取所有部门列表
     */
    List<String> getAllDepartments();
    
    /**
     * 获取所有职称列表
     */
    List<String> getAllTitles();
    
    /**
     * 批量删除教师
     */
    void batchDeleteTeachers(List<Long> ids);
}
