package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.common.result.Result;
import com.student.dto.ClassCreateRequest;
import com.student.dto.ClassResponse;
import com.student.dto.ClassUpdateRequest;
import com.student.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "班级管理", description = "班级信息的增删改查操作")
@RestController
@RequestMapping("/api/classes")
public class ClassController {
    
    @Autowired
    private ClassService classService;
    
    /**
     * 创建班级
     */
    @Operation(summary = "创建班级", description = "添加新的班级信息")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<ClassResponse> createClass(
            @Parameter(description = "班级创建请求信息", required = true)
            @Valid @RequestBody ClassCreateRequest request) {
        ClassResponse response = classService.createClass(request);
        return Result.success("创建班级成功", response);
    }
    
    /**
     * 更新班级信息
     */
    @Operation(summary = "更新班级信息", description = "根据班级ID更新班级信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<ClassResponse> updateClass(
            @Parameter(description = "班级ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "班级更新请求信息", required = true)
            @Valid @RequestBody ClassUpdateRequest request) {
        ClassResponse response = classService.updateClass(id, request);
        return Result.success("更新班级信息成功", response);
    }
    
    /**
     * 删除班级
     */
    @Operation(summary = "删除班级", description = "根据班级ID删除班级（仅管理员）")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteClass(
            @Parameter(description = "班级ID", required = true, example = "1")
            @PathVariable Long id) {
        classService.deleteClass(id);
        return Result.success();
    }
    
    /**
     * 获取班级详情
     */
    @Operation(summary = "获取班级详情", description = "根据班级ID获取班级详细信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<ClassResponse> getClass(
            @Parameter(description = "班级ID", required = true, example = "1")
            @PathVariable Long id) {
        ClassResponse response = classService.getClassById(id);
        return Result.success(response);
    }
    
    /**
     * 分页查询班级列表
     */
    @Operation(summary = "分页查询班级列表", description = "支持按班级名称、专业、年级等条件筛选")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<IPage<ClassResponse>> getClassList(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "班级名称", example = "计算机1班")
            @RequestParam(required = false) String className,
            @Parameter(description = "专业", example = "计算机科学与技术")
            @RequestParam(required = false) String major,
            @Parameter(description = "年级", example = "2021")
            @RequestParam(required = false) String grade,
            @Parameter(description = "入学年份", example = "2021")
            @RequestParam(required = false) Integer enrollYear) {
        IPage<ClassResponse> result = classService.getClassList(
                page, size, className, major, grade, enrollYear);
        return Result.success(result);
    }
    
    /**
     * 根据专业获取班级列表
     */
    @GetMapping("/by-major/{major}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<List<ClassResponse>> getClassesByMajor(@PathVariable String major) {
        List<ClassResponse> classes = classService.getClassesByMajor(major);
        return Result.success(classes);
    }
    
    /**
     * 根据年级获取班级列表
     */
    @GetMapping("/by-grade/{grade}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<List<ClassResponse>> getClassesByGrade(@PathVariable String grade) {
        List<ClassResponse> classes = classService.getClassesByGrade(grade);
        return Result.success(classes);
    }
    
    /**
     * 获取所有专业列表
     */
    @GetMapping("/majors")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<List<String>> getAllMajors() {
        List<String> majors = classService.getAllMajors();
        return Result.success(majors);
    }
    
    /**
     * 获取所有年级列表
     */
    @GetMapping("/grades")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<List<String>> getAllGrades() {
        List<String> grades = classService.getAllGrades();
        return Result.success(grades);
    }
    
    /**
     * 批量删除班级
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> batchDeleteClasses(@RequestBody List<Long> ids) {
        classService.batchDeleteClasses(ids);
        return Result.success();
    }
}
