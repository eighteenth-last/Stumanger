package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.common.result.Result;
import com.student.dto.ScoreCreateRequest;
import com.student.dto.ScoreResponse;
import com.student.dto.ScoreUpdateRequest;
import com.student.service.ExcelService;
import com.student.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Tag(name = "成绩管理", description = "成绩信息的增删改查、统计分析、导入导出等操作")
@RestController
@RequestMapping("/api/scores")
public class ScoreController {
    
    @Autowired
    private ScoreService scoreService;
    
    @Autowired
    private ExcelService excelService;
    
    /**
     * 创建成绩记录
     */
    @Operation(summary = "创建成绩记录", description = "添加新的成绩记录")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<ScoreResponse> createScore(
            @Parameter(description = "成绩创建请求信息", required = true)
            @Valid @RequestBody ScoreCreateRequest request) {
        ScoreResponse response = scoreService.createScore(request);
        return Result.success("创建成绩记录成功", response);
    }
    
    /**
     * 更新成绩
     */
    @Operation(summary = "更新成绩", description = "根据成绩ID更新成绩信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<ScoreResponse> updateScore(
            @Parameter(description = "成绩ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "成绩更新请求信息", required = true)
            @Valid @RequestBody ScoreUpdateRequest request) {
        ScoreResponse response = scoreService.updateScore(id, request);
        return Result.success("更新成绩成功", response);
    }
    
    /**
     * 删除成绩记录
     */
    @Operation(summary = "删除成绩记录", description = "根据成绩ID删除成绩记录")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> deleteScore(
            @Parameter(description = "成绩ID", required = true, example = "1")
            @PathVariable Long id) {
        scoreService.deleteScore(id);
        return Result.success();
    }
    
    /**
     * 获取成绩详情
     */
    @Operation(summary = "获取成绩详情", description = "根据成绩ID获取成绩详细信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<ScoreResponse> getScore(
            @Parameter(description = "成绩ID", required = true, example = "1")
            @PathVariable Long id) {
        ScoreResponse response = scoreService.getScoreById(id);
        return Result.success(response);
    }
    
    /**
     * 分页查询成绩列表
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<IPage<ScoreResponse>> getScoreList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) BigDecimal minScore,
            @RequestParam(required = false) BigDecimal maxScore) {
        IPage<ScoreResponse> result = scoreService.getScoreList(
                page, size, studentId, courseId, semester, minScore, maxScore);
        return Result.success(result);
    }
    
    /**
     * 根据学生ID获取成绩列表
     */
    @GetMapping("/by-student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<List<ScoreResponse>> getScoresByStudent(@PathVariable Long studentId) {
        List<ScoreResponse> scores = scoreService.getScoresByStudentId(studentId);
        return Result.success(scores);
    }
    
    /**
     * 根据课程ID获取成绩列表
     */
    @GetMapping("/by-course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<ScoreResponse>> getScoresByCourse(@PathVariable Long courseId) {
        List<ScoreResponse> scores = scoreService.getScoresByCourseId(courseId);
        return Result.success(scores);
    }
    
    /**
     * 获取课程成绩统计
     */
    @GetMapping("/statistics/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Map<String, Object>> getCourseScoreStatistics(
            @PathVariable Long courseId,
            @RequestParam(required = false) String semester) {
        Map<String, Object> statistics = scoreService.getCourseScoreStatistics(courseId, semester);
        return Result.success(statistics);
    }
    
    /**
     * 获取学生成绩统计
     */
    @GetMapping("/statistics/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<Map<String, Object>> getStudentScoreStatistics(@PathVariable Long studentId) {
        Map<String, Object> statistics = scoreService.getStudentScoreStatistics(studentId);
        return Result.success(statistics);
    }
    
    /**
     * 获取班级成绩统计
     */
    @GetMapping("/statistics/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<Map<String, Object>>> getClassScoreStatistics(
            @PathVariable Long classId,
            @RequestParam(required = false) String semester) {
        List<Map<String, Object>> statistics = scoreService.getClassScoreStatistics(classId, semester);
        return Result.success(statistics);
    }
    
    /**
     * 获取所有学期列表
     */
    @GetMapping("/semesters")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<List<String>> getAllSemesters() {
        List<String> semesters = scoreService.getAllSemesters();
        return Result.success(semesters);
    }
    
    /**
     * 批量删除成绩
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> batchDeleteScores(@RequestBody List<Long> ids) {
        scoreService.batchDeleteScores(ids);
        return Result.success();
    }
    
    /**
     * 批量导入成绩
     */
    @PostMapping("/batch-import")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> batchImportScores(@Valid @RequestBody List<ScoreCreateRequest> requests) {
        scoreService.batchImportScores(requests);
        return Result.success();
    }
    
    /**
     * 从Excel导入成绩
     */
    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> importScores(@RequestParam("file") MultipartFile file) {
        try {
            excelService.importScores(file);
            return Result.success();
        } catch (IOException e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出成绩数据
     */
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<byte[]> exportScores(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String semester) {
        try {
            byte[] data = excelService.exportScores(studentId, courseId, semester);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "scores.xlsx");
            return ResponseEntity.ok().headers(headers).body(data);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 下载成绩导入模板
     */
    @GetMapping("/template")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<byte[]> downloadScoreTemplate() {
        try {
            byte[] data = excelService.exportScoreTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "score_template.xlsx");
            return ResponseEntity.ok().headers(headers).body(data);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
