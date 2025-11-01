package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.common.result.Result;
import com.student.dto.TeacherCreateRequest;
import com.student.dto.TeacherResponse;
import com.student.dto.TeacherUpdateRequest;
import com.student.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "教师管理", description = "教师信息的增删改查操作")
@RestController
@RequestMapping("/api/teachers")
public class TeacherController {
    
    @Autowired
    private TeacherService teacherService;
    
    /**
     * 创建教师
     */
    @Operation(summary = "创建教师", description = "添加新的教师信息（仅管理员）")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<TeacherResponse> createTeacher(
            @Parameter(description = "教师创建请求信息", required = true)
            @Valid @RequestBody TeacherCreateRequest request) {
        TeacherResponse response = teacherService.createTeacher(request);
        return Result.success("创建教师成功", response);
    }
    
    /**
     * 更新教师信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<TeacherResponse> updateTeacher(@PathVariable Long id,
                                                  @Valid @RequestBody TeacherUpdateRequest request) {
        TeacherResponse response = teacherService.updateTeacher(id, request);
        return Result.success("更新教师信息成功", response);
    }
    
    /**
     * 删除教师
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return Result.success();
    }
    
    /**
     * 获取教师详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<TeacherResponse> getTeacher(@PathVariable Long id) {
        TeacherResponse response = teacherService.getTeacherById(id);
        return Result.success(response);
    }
    
    /**
     * 根据教师编号获取教师
     */
    @GetMapping("/by-teacher-no/{teacherNo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<TeacherResponse> getTeacherByTeacherNo(@PathVariable String teacherNo) {
        TeacherResponse response = teacherService.getTeacherByTeacherNo(teacherNo);
        return Result.success(response);
    }
    
    /**
     * 分页查询教师列表
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<IPage<TeacherResponse>> getTeacherList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String teacherNo,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String title) {
        IPage<TeacherResponse> result = teacherService.getTeacherList(
                page, size, teacherNo, realName, department, title);
        return Result.success(result);
    }
    
    /**
     * 根据部门获取教师列表
     */
    @GetMapping("/by-department/{department}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<TeacherResponse>> getTeachersByDepartment(@PathVariable String department) {
        List<TeacherResponse> teachers = teacherService.getTeachersByDepartment(department);
        return Result.success(teachers);
    }
    
    /**
     * 根据职称获取教师列表
     */
    @GetMapping("/by-title/{title}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<TeacherResponse>> getTeachersByTitle(@PathVariable String title) {
        List<TeacherResponse> teachers = teacherService.getTeachersByTitle(title);
        return Result.success(teachers);
    }
    
    /**
     * 获取所有部门列表
     */
    @GetMapping("/departments")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<String>> getAllDepartments() {
        List<String> departments = teacherService.getAllDepartments();
        return Result.success(departments);
    }
    
    /**
     * 获取所有职称列表
     */
    @GetMapping("/titles")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<String>> getAllTitles() {
        List<String> titles = teacherService.getAllTitles();
        return Result.success(titles);
    }
    
    /**
     * 批量删除教师
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> batchDeleteTeachers(@RequestBody List<Long> ids) {
        teacherService.batchDeleteTeachers(ids);
        return Result.success();
    }
}
