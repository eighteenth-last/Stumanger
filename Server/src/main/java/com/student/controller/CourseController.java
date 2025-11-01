package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.common.result.Result;
import com.student.dto.CourseCreateRequest;
import com.student.dto.CourseResponse;
import com.student.dto.CourseUpdateRequest;
import com.student.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "课程管理", description = "课程信息的增删改查操作")
@RestController
@RequestMapping("/api/courses")
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    /**
     * 创建课程
     */
    @Operation(summary = "创建课程", description = "添加新的课程信息")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<CourseResponse> createCourse(
            @Parameter(description = "课程创建请求信息", required = true)
            @Valid @RequestBody CourseCreateRequest request) {
        CourseResponse response = courseService.createCourse(request);
        return Result.success("创建课程成功", response);
    }
    
    /**
     * 更新课程信息
     */
    @Operation(summary = "更新课程信息", description = "根据课程ID更新课程信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<CourseResponse> updateCourse(
            @Parameter(description = "课程ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "课程更新请求信息", required = true)
            @Valid @RequestBody CourseUpdateRequest request) {
        CourseResponse response = courseService.updateCourse(id, request);
        return Result.success("更新课程信息成功", response);
    }
    
    /**
     * 删除课程
     */
    @Operation(summary = "删除课程", description = "根据课程ID删除课程（仅管理员）")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteCourse(
            @Parameter(description = "课程ID", required = true, example = "1")
            @PathVariable Long id) {
        courseService.deleteCourse(id);
        return Result.success();
    }
    
    /**
     * 获取课程详情
     */
    @Operation(summary = "获取课程详情", description = "根据课程ID获取课程详细信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<CourseResponse> getCourse(
            @Parameter(description = "课程ID", required = true, example = "1")
            @PathVariable Long id) {
        CourseResponse response = courseService.getCourseById(id);
        return Result.success(response);
    }
    
    /**
     * 根据课程代码获取课程
     */
    @GetMapping("/by-course-code/{courseCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<CourseResponse> getCourseByCourseCode(@PathVariable String courseCode) {
        CourseResponse response = courseService.getCourseByCourseCode(courseCode);
        return Result.success(response);
    }
    
    /**
     * 分页查询课程列表
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<IPage<CourseResponse>> getCourseList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) Long teacherId) {
        IPage<CourseResponse> result = courseService.getCourseList(
                page, size, courseName, courseCode, teacherId);
        return Result.success(result);
    }
    
    /**
     * 根据教师ID获取课程列表
     */
    @GetMapping("/by-teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<CourseResponse>> getCoursesByTeacher(@PathVariable Long teacherId) {
        List<CourseResponse> courses = courseService.getCoursesByTeacherId(teacherId);
        return Result.success(courses);
    }
    
    /**
     * 批量删除课程
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> batchDeleteCourses(@RequestBody List<Long> ids) {
        courseService.batchDeleteCourses(ids);
        return Result.success();
    }
}
