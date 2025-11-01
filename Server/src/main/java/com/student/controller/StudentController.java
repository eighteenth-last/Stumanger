package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.common.result.Result;
import com.student.dto.StudentCreateRequest;
import com.student.dto.StudentResponse;
import com.student.dto.StudentUpdateRequest;
import com.student.entity.enums.Gender;
import com.student.service.ExcelService;
import com.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "学生管理", description = "学生信息的增删改查、导入导出等操作")
@RestController
@RequestMapping("/api/students")
public class StudentController {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private ExcelService excelService;
    
    /**
     * 创建学生
     */
    @Operation(summary = "创建学生", description = "添加新的学生信息到系统")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功",
            content = @Content(schema = @Schema(implementation = StudentResponse.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<StudentResponse> createStudent(
            @Parameter(description = "学生创建请求信息", required = true)
            @Valid @RequestBody StudentCreateRequest request) {
        StudentResponse response = studentService.createStudent(request);
        return Result.success("创建学生成功", response);
    }
    
    /**
     * 更新学生信息
     */
    @Operation(summary = "更新学生信息", description = "根据学生ID更新学生的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功",
            content = @Content(schema = @Schema(implementation = StudentResponse.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "学生不存在")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<StudentResponse> updateStudent(
            @Parameter(description = "学生ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "学生更新请求信息", required = true)
            @Valid @RequestBody StudentUpdateRequest request) {
        StudentResponse response = studentService.updateStudent(id, request);
        return Result.success("更新学生信息成功", response);
    }
    
    /**
     * 删除学生
     */
    @Operation(summary = "删除学生", description = "根据学生ID删除学生信息（仅管理员）")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "学生不存在")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteStudent(
            @Parameter(description = "学生ID", required = true, example = "1")
            @PathVariable Long id) {
        studentService.deleteStudent(id);
        return Result.success();
    }
    
    /**
     * 获取学生详情
     */
    @Operation(summary = "获取学生详情", description = "根据学生ID获取学生的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功",
            content = @Content(schema = @Schema(implementation = StudentResponse.class))),
        @ApiResponse(responseCode = "404", description = "学生不存在")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public Result<StudentResponse> getStudent(
            @Parameter(description = "学生ID", required = true, example = "1")
            @PathVariable Long id) {
        StudentResponse response = studentService.getStudentById(id);
        return Result.success(response);
    }
    
    /**
     * 根据学号获取学生
     */
    @Operation(summary = "根据学号获取学生", description = "通过学号查询学生信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功",
            content = @Content(schema = @Schema(implementation = StudentResponse.class))),
        @ApiResponse(responseCode = "404", description = "学生不存在")
    })
    @GetMapping("/by-student-no/{studentNo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<StudentResponse> getStudentByStudentNo(
            @Parameter(description = "学号", required = true, example = "2021001")
            @PathVariable String studentNo) {
        StudentResponse response = studentService.getStudentByStudentNo(studentNo);
        return Result.success(response);
    }
    
    /**
     * 分页查询学生列表
     */
    @Operation(summary = "分页查询学生列表", description = "支持按学号、姓名、班级、性别等条件筛选")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<IPage<StudentResponse>> getStudentList(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "学号（模糊查询）", example = "2021")
            @RequestParam(required = false) String studentNo,
            @Parameter(description = "姓名（模糊查询）", example = "张三")
            @RequestParam(required = false) String realName,
            @Parameter(description = "班级ID", example = "1")
            @RequestParam(required = false) Long classId,
            @Parameter(description = "性别", schema = @Schema(implementation = Gender.class))
            @RequestParam(required = false) Gender gender) {
        IPage<StudentResponse> result = studentService.getStudentList(
                page, size, studentNo, realName, classId, gender);
        return Result.success(result);
    }
    
    /**
     * 根据班级ID获取学生列表
     */
    @GetMapping("/by-class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<StudentResponse>> getStudentsByClass(@PathVariable Long classId) {
        List<StudentResponse> students = studentService.getStudentsByClassId(classId);
        return Result.success(students);
    }
    
    /**
     * 获取未分配班级的学生
     */
    @GetMapping("/without-class")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<StudentResponse>> getStudentsWithoutClass() {
        List<StudentResponse> students = studentService.getStudentsWithoutClass();
        return Result.success(students);
    }
    
    /**
     * 批量分配班级
     */
    @PostMapping("/batch-assign-class")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> batchAssignClass(@RequestBody BatchAssignClassRequest request) {
        studentService.batchAssignClass(request.getStudentIds(), request.getClassId());
        return Result.success();
    }
    
    /**
     * 批量删除学生
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> batchDeleteStudents(@RequestBody List<Long> ids) {
        studentService.batchDeleteStudents(ids);
        return Result.success();
    }
    
    /**
     * 导入学生数据
     */
    @Operation(summary = "导入学生数据", description = "通过Excel文件批量导入学生信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "导入成功"),
        @ApiResponse(responseCode = "400", description = "文件格式错误或数据不合法"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> importStudents(
            @Parameter(description = "Excel文件", required = true)
            @RequestParam("file") MultipartFile file) {
        try {
            excelService.importStudents(file);
            return Result.success();
        } catch (IOException e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出学生数据
     */
    @Operation(summary = "导出学生数据", description = "导出学生信息为Excel文件，支持按条件筛选")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "导出成功"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<byte[]> exportStudents(
            @Parameter(description = "班级ID", example = "1")
            @RequestParam(required = false) Long classId,
            @Parameter(description = "学号", example = "2021001")
            @RequestParam(required = false) String studentNo,
            @Parameter(description = "姓名", example = "张三")
            @RequestParam(required = false) String realName) {
        try {
            logger.info("收到导出请求 - classId: {}, studentNo: {}, realName: {}", classId, studentNo, realName);
            if (classId == null && studentNo == null && realName == null) {
                logger.info("未指定筛选条件，将导出所有学生数据");
            }
            byte[] data = excelService.exportStudents(classId, studentNo, realName);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "students_" + System.currentTimeMillis() + ".xlsx");
            logger.info("导出成功，返回数据大小: {} bytes", data.length);
            return ResponseEntity.ok().headers(headers).body(data);
        } catch (Exception e) {
            logger.error("导出学生数据失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 下载学生导入模板
     */
    @Operation(summary = "下载学生导入模板", description = "下载Excel模板文件，用于批量导入学生")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "下载成功"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @GetMapping("/template")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<byte[]> downloadStudentTemplate() {
        try {
            byte[] data = excelService.exportStudentTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "student_template.xlsx");
            return ResponseEntity.ok().headers(headers).body(data);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 批量分配班级请求
     */
    public static class BatchAssignClassRequest {
        private List<Long> studentIds;
        private Long classId;
        
        public List<Long> getStudentIds() {
            return studentIds;
        }
        
        public void setStudentIds(List<Long> studentIds) {
            this.studentIds = studentIds;
        }
        
        public Long getClassId() {
            return classId;
        }
        
        public void setClassId(Long classId) {
            this.classId = classId;
        }
    }
}
