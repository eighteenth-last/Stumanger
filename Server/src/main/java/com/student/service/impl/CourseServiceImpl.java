package com.student.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.common.exception.BusinessException;
import com.student.dto.CourseCreateRequest;
import com.student.dto.CourseResponse;
import com.student.dto.CourseUpdateRequest;
import com.student.entity.Course;
import com.student.mapper.CourseMapper;
import com.student.mapper.ScoreMapper;
import com.student.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {
    
    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private ScoreMapper scoreMapper;
    
    @Override
    @Transactional
    public CourseResponse createCourse(CourseCreateRequest request) {
        // 检查课程代码是否已存在
        if (courseMapper.existsByCourseCode(request.getCourseCode(), null)) {
            throw new BusinessException("课程代码已存在");
        }
        
        Course course = new Course();
        course.setCourseName(request.getCourseName());
        course.setCourseCode(request.getCourseCode());
        course.setCredit(request.getCredit());
        course.setHours(request.getHours());
        course.setSemester(request.getSemester());
        course.setClassroom(request.getClassroom());
        course.setClassTime(request.getClassTime());
        course.setTeacherId(request.getTeacherId());
        course.setDescription(request.getDescription());
        
        courseMapper.insert(course);
        log.info("创建课程成功: {}", course.getCourseName());
        
        return getCourseById(course.getId());
    }
    
    @Override
    @Transactional
    public CourseResponse updateCourse(Long id, CourseUpdateRequest request) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        if (request.getCourseName() != null) {
            course.setCourseName(request.getCourseName());
        }
        if (request.getCredit() != null) {
            course.setCredit(request.getCredit());
        }
        if (request.getHours() != null) {
            course.setHours(request.getHours());
        }
        if (request.getSemester() != null) {
            course.setSemester(request.getSemester());
        }
        if (request.getClassroom() != null) {
            course.setClassroom(request.getClassroom());
        }
        if (request.getClassTime() != null) {
            course.setClassTime(request.getClassTime());
        }
        if (request.getTeacherId() != null) {
            course.setTeacherId(request.getTeacherId());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        
        courseMapper.updateById(course);
        log.info("更新课程信息成功: {}", course.getCourseName());
        
        return getCourseById(id);
    }
    
    @Override
    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        // 检查是否有成绩记录
        Long studentCount = scoreMapper.countStudentsByCourseId(id);
        if (studentCount > 0) {
            throw new BusinessException("该课程已有成绩记录，无法删除");
        }
        
        courseMapper.deleteById(id);
        log.info("删除课程成功: {}", course.getCourseName());
    }
    
    @Override
    public CourseResponse getCourseById(Long id) {
        Course course = courseMapper.selectCourseWithDetails(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        return convertToResponse(course);
    }
    
    @Override
    public CourseResponse getCourseByCourseCode(String courseCode) {
        Course course = courseMapper.findByCourseCode(courseCode);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        return getCourseById(course.getId());
    }
    
    @Override
    public IPage<CourseResponse> getCourseList(Integer page, Integer size, String courseName,
                                               String courseCode, Long teacherId) {
        Page<Course> pageParam = new Page<>(page, size);
        IPage<Course> coursePage = courseMapper.selectCoursesWithDetails(
                pageParam, courseName, courseCode, teacherId);
        
        return coursePage.convert(this::convertToResponse);
    }
    
    @Override
    public List<CourseResponse> getCoursesByTeacherId(Long teacherId) {
        List<Course> courses = courseMapper.findByTeacherId(teacherId);
        return courses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void batchDeleteCourses(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("课程ID列表不能为空");
        }
        
        for (Long id : ids) {
            deleteCourse(id);
        }
        log.info("批量删除课程成功，共{}个课程", ids.size());
    }
    
    private CourseResponse convertToResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setCourseName(course.getCourseName());
        response.setCourseCode(course.getCourseCode());
        response.setCredit(course.getCredit());
        response.setHours(course.getHours());
        response.setSemester(course.getSemester());
        response.setClassroom(course.getClassroom());
        response.setClassTime(course.getClassTime());
        response.setTeacherId(course.getTeacherId());
        response.setDescription(course.getDescription());
        response.setCreateTime(course.getCreateTime());
        response.setUpdateTime(course.getUpdateTime());
        
        // 设置教师信息
        if (course.getTeacher() != null) {
            response.setTeacherNo(course.getTeacher().getTeacherNo());
            response.setDepartment(course.getTeacher().getDepartment());
            if (course.getTeacher().getUser() != null) {
                response.setTeacherName(course.getTeacher().getUser().getRealName());
            }
        }
        
        // 统计选课学生数
        Long studentCount = scoreMapper.countStudentsByCourseId(course.getId());
        response.setStudentCount(studentCount.intValue());
        
        return response;
    }
}
