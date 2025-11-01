package com.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.dto.CourseCreateRequest;
import com.student.dto.CourseResponse;
import com.student.dto.CourseUpdateRequest;

import java.util.List;

public interface CourseService {
    
    /**
     * 创建课程
     */
    CourseResponse createCourse(CourseCreateRequest request);
    
    /**
     * 更新课程信息
     */
    CourseResponse updateCourse(Long id, CourseUpdateRequest request);
    
    /**
     * 删除课程
     */
    void deleteCourse(Long id);
    
    /**
     * 根据ID获取课程详情
     */
    CourseResponse getCourseById(Long id);
    
    /**
     * 根据课程代码获取课程
     */
    CourseResponse getCourseByCourseCode(String courseCode);
    
    /**
     * 分页查询课程列表
     */
    IPage<CourseResponse> getCourseList(Integer page, Integer size, String courseName,
                                        String courseCode, Long teacherId);
    
    /**
     * 根据教师ID获取课程列表
     */
    List<CourseResponse> getCoursesByTeacherId(Long teacherId);
    
    /**
     * 批量删除课程
     */
    void batchDeleteCourses(List<Long> ids);
}
