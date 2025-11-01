package com.student.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.common.exception.BusinessException;
import com.student.common.utils.ExcelUtils;
import com.student.dto.ScoreCreateRequest;
import com.student.dto.StudentCreateRequest;
import com.student.entity.Course;
import com.student.entity.Score;
import com.student.entity.Student;
import com.student.entity.enums.Gender;
import com.student.mapper.CourseMapper;
import com.student.mapper.ScoreMapper;
import com.student.mapper.StudentMapper;
import com.student.service.ExcelService;
import com.student.service.ScoreService;
import com.student.service.StudentService;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExcelServiceImpl implements ExcelService {
    
    private static final Logger log = LoggerFactory.getLogger(ExcelServiceImpl.class);
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private ScoreService scoreService;
    
    @Autowired
    private StudentMapper studentMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private ScoreMapper scoreMapper;
    
    @Override
    @Transactional
    public void importStudents(MultipartFile file) throws IOException {
        if (!ExcelUtils.isValidExcelFile(file)) {
            throw new BusinessException("无效的Excel文件格式");
        }
        
        Workbook workbook = ExcelUtils.readExcel(file);
        Sheet sheet = workbook.getSheetAt(0);
        
        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();
        
        // 跳过表头，从第二行开始读取
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            
            try {
                StudentCreateRequest request = parseStudentRow(row);
                studentService.createStudent(request);
                successCount++;
            } catch (Exception e) {
                failCount++;
                errors.add(String.format("第%d行导入失败: %s", i + 1, e.getMessage()));
                log.warn("导入学生失败，行号: {}, 原因: {}", i + 1, e.getMessage());
            }
        }
        
        workbook.close();
        log.info("学生数据导入完成: 成功{}条, 失败{}条", successCount, failCount);
        
        if (failCount > 0 && errors.size() <= 10) {
            throw new BusinessException("部分数据导入失败: " + String.join("; ", errors));
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public byte[] exportStudents(Long classId, String studentNo, String realName) throws IOException {
        log.info("开始导出学生数据 - classId: {}, studentNo: {}, realName: {}", classId, studentNo, realName);
        
        List<Student> students = new ArrayList<>();
        
        try {
            // 使用分页查询获取所有学生（包含详细信息）
            int pageNum = 1;
            int pageSize = 1000;
            IPage<Student> page;
            
            do {
                page = studentMapper.selectStudentsWithDetails(
                    new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize),
                    studentNo,
                    realName,
                    classId,
                    null
                );
                students.addAll(page.getRecords());
                pageNum++;
            } while (page.getCurrent() < page.getPages());
            
            log.info("查询到 {} 条学生记录", students.size());
        } catch (Exception e) {
            log.error("查询学生数据失败", e);
            throw new IOException("查询学生数据失败: " + e.getMessage(), e);
        }
        
        Workbook workbook = null;
        try {
            log.info("开始创建Excel工作簿");
            workbook = ExcelUtils.createWorkbook();
            log.info("Excel工作簿创建成功");
            
            Sheet sheet = workbook.createSheet("学生信息");
            log.info("工作表创建成功");
            
            // 创建样式
            CellStyle headerStyle = ExcelUtils.createHeaderStyle(workbook);
            CellStyle dataStyle = ExcelUtils.createDataStyle(workbook);
            log.info("样式创建成功");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "性别", "班级", "出生日期", "邮箱", "电话", "地址"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4000);
            }
            log.info("表头创建成功");
            
            // 填充数据
            int rowNum = 1;
            log.info("开始填充 {} 条学生数据到Excel", students.size());
            for (Student student : students) {
                try {
                    Row row = sheet.createRow(rowNum++);
                    
                    Cell cell0 = row.createCell(0);
                    ExcelUtils.setCellValue(cell0, student.getStudentNo());
                    cell0.setCellStyle(dataStyle);
                    
                    Cell cell1 = row.createCell(1);
                    ExcelUtils.setCellValue(cell1, student.getUser() != null ? student.getUser().getRealName() : "");
                    cell1.setCellStyle(dataStyle);
                    
                    Cell cell2 = row.createCell(2);
                    ExcelUtils.setCellValue(cell2, student.getGender() != null ? 
                            (student.getGender() == Gender.MALE ? "男" : "女") : "");
                    cell2.setCellStyle(dataStyle);
                    
                    Cell cell3 = row.createCell(3);
                    ExcelUtils.setCellValue(cell3, student.getStudentClass() != null ? 
                            student.getStudentClass().getClassName() : "");
                    cell3.setCellStyle(dataStyle);
                    
                    Cell cell4 = row.createCell(4);
                    ExcelUtils.setCellValue(cell4, student.getBirthDate());
                    cell4.setCellStyle(dataStyle);
                    
                    Cell cell5 = row.createCell(5);
                    ExcelUtils.setCellValue(cell5, student.getUser() != null ? student.getUser().getEmail() : "");
                    cell5.setCellStyle(dataStyle);
                    
                    Cell cell6 = row.createCell(6);
                    ExcelUtils.setCellValue(cell6, student.getUser() != null ? student.getUser().getPhone() : "");
                    cell6.setCellStyle(dataStyle);
                    
                    Cell cell7 = row.createCell(7);
                    ExcelUtils.setCellValue(cell7, student.getAddress());
                    cell7.setCellStyle(dataStyle);
                } catch (Exception e) {
                    log.error("处理学生数据失败，ID: {}", student.getId(), e);
                    // 继续处理下一条记录
                }
            }
            
            log.info("Excel数据填充完成，共 {} 行数据（不含表头）", rowNum - 1);
            byte[] result = ExcelUtils.workbookToBytes(workbook);
            log.info("Excel文件生成成功，大小: {} bytes", result.length);
            return result;
        } catch (Exception e) {
            log.error("导出Excel失败", e);
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException ex) {
                    log.error("关闭工作簿失败", ex);
                }
            }
            throw new IOException("导出Excel失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public void importScores(MultipartFile file) throws IOException {
        if (!ExcelUtils.isValidExcelFile(file)) {
            throw new BusinessException("无效的Excel文件格式");
        }
        
        Workbook workbook = ExcelUtils.readExcel(file);
        Sheet sheet = workbook.getSheetAt(0);
        
        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();
        
        // 跳过表头，从第二行开始读取
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            
            try {
                ScoreCreateRequest request = parseScoreRow(row);
                scoreService.createScore(request);
                successCount++;
            } catch (Exception e) {
                failCount++;
                errors.add(String.format("第%d行导入失败: %s", i + 1, e.getMessage()));
                log.warn("导入成绩失败，行号: {}, 原因: {}", i + 1, e.getMessage());
            }
        }
        
        workbook.close();
        log.info("成绩数据导入完成: 成功{}条, 失败{}条", successCount, failCount);
        
        if (failCount > 0 && errors.size() <= 10) {
            throw new BusinessException("部分数据导入失败: " + String.join("; ", errors));
        }
    }
    
    @Override
    public byte[] exportScores(Long studentId, Long courseId, String semester) throws IOException {
        List<Score> scores;
        
        if (studentId != null) {
            scores = scoreMapper.findByStudentId(studentId);
        } else if (courseId != null) {
            scores = scoreMapper.findByCourseId(courseId);
        } else {
            scores = scoreMapper.selectList(null);
        }
        
        Workbook workbook = ExcelUtils.createWorkbook();
        Sheet sheet = workbook.createSheet("成绩信息");
        
        // 创建样式
        CellStyle headerStyle = ExcelUtils.createHeaderStyle(workbook);
        CellStyle dataStyle = ExcelUtils.createDataStyle(workbook);
        
        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"学号", "学生姓名", "课程代码", "课程名称", "成绩", "学期"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4000);
        }
        
        // 填充数据
        int rowNum = 1;
        for (Score score : scores) {
            Score detailedScore = scoreMapper.selectScoreWithDetails(score.getId());
            Row row = sheet.createRow(rowNum++);
            
            Cell cell0 = row.createCell(0);
            ExcelUtils.setCellValue(cell0, detailedScore.getStudent() != null ? 
                    detailedScore.getStudent().getStudentNo() : "");
            cell0.setCellStyle(dataStyle);
            
            Cell cell1 = row.createCell(1);
            ExcelUtils.setCellValue(cell1, detailedScore.getStudent() != null && 
                    detailedScore.getStudent().getUser() != null ? 
                    detailedScore.getStudent().getUser().getRealName() : "");
            cell1.setCellStyle(dataStyle);
            
            Cell cell2 = row.createCell(2);
            ExcelUtils.setCellValue(cell2, detailedScore.getCourse() != null ? 
                    detailedScore.getCourse().getCourseCode() : "");
            cell2.setCellStyle(dataStyle);
            
            Cell cell3 = row.createCell(3);
            ExcelUtils.setCellValue(cell3, detailedScore.getCourse() != null ? 
                    detailedScore.getCourse().getCourseName() : "");
            cell3.setCellStyle(dataStyle);
            
            Cell cell4 = row.createCell(4);
            ExcelUtils.setCellValue(cell4, detailedScore.getScore());
            cell4.setCellStyle(dataStyle);
            
            Cell cell5 = row.createCell(5);
            ExcelUtils.setCellValue(cell5, detailedScore.getSemester());
            cell5.setCellStyle(dataStyle);
        }
        
        return ExcelUtils.workbookToBytes(workbook);
    }
    
    @Override
    public byte[] exportStudentTemplate() throws IOException {
        Workbook workbook = ExcelUtils.createWorkbook();
        Sheet sheet = workbook.createSheet("学生信息模板");
        
        CellStyle headerStyle = ExcelUtils.createHeaderStyle(workbook);
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"用户名*", "密码*", "真实姓名*", "学号*", "班级ID", "性别*", "出生日期", "邮箱", "电话", "地址"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4000);
        }

        
        return ExcelUtils.workbookToBytes(workbook);
    }
    
    @Override
    public byte[] exportScoreTemplate() throws IOException {
        Workbook workbook = ExcelUtils.createWorkbook();
        Sheet sheet = workbook.createSheet("成绩信息模板");
        
        CellStyle headerStyle = ExcelUtils.createHeaderStyle(workbook);
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"学号*", "课程代码*", "成绩*", "学期*"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4000);
        }

        
        return ExcelUtils.workbookToBytes(workbook);
    }
    
    private StudentCreateRequest parseStudentRow(Row row) {
        StudentCreateRequest request = new StudentCreateRequest();
        
        request.setUsername(ExcelUtils.getCellStringValue(row.getCell(0)));
        request.setPassword(ExcelUtils.getCellStringValue(row.getCell(1)));
        request.setRealName(ExcelUtils.getCellStringValue(row.getCell(2)));
        request.setStudentNo(ExcelUtils.getCellStringValue(row.getCell(3)));
        
        String classIdStr = ExcelUtils.getCellStringValue(row.getCell(4));
        if (classIdStr != null && !classIdStr.isEmpty()) {
            request.setClassId(Long.parseLong(classIdStr));
        }
        
        String genderStr = ExcelUtils.getCellStringValue(row.getCell(5));
        if (genderStr != null) {
            request.setGender(Gender.valueOf(genderStr.toUpperCase()));
        }
        
        LocalDate birthDate = ExcelUtils.getCellDateValue(row.getCell(6));
        if (birthDate == null) {
            String dateStr = ExcelUtils.getCellStringValue(row.getCell(6));
            if (dateStr != null && !dateStr.isEmpty()) {
                birthDate = LocalDate.parse(dateStr);
            }
        }
        request.setBirthDate(birthDate);
        
        request.setEmail(ExcelUtils.getCellStringValue(row.getCell(7)));
        request.setPhone(ExcelUtils.getCellStringValue(row.getCell(8)));
        request.setAddress(ExcelUtils.getCellStringValue(row.getCell(9)));
        
        return request;
    }
    
    private ScoreCreateRequest parseScoreRow(Row row) {
        ScoreCreateRequest request = new ScoreCreateRequest();
        
        // 根据学号查找学生ID
        String studentNo = ExcelUtils.getCellStringValue(row.getCell(0));
        Student student = studentMapper.findByStudentNo(studentNo);
        if (student == null) {
            throw new BusinessException("学号不存在: " + studentNo);
        }
        request.setStudentId(student.getId());
        
        // 根据课程代码查找课程ID
        String courseCode = ExcelUtils.getCellStringValue(row.getCell(1));
        Course course = courseMapper.findByCourseCode(courseCode);
        if (course == null) {
            throw new BusinessException("课程代码不存在: " + courseCode);
        }
        request.setCourseId(course.getId());
        
        Double scoreValue = ExcelUtils.getCellNumericValue(row.getCell(2));
        if (scoreValue == null) {
            throw new BusinessException("成绩不能为空");
        }
        request.setScore(BigDecimal.valueOf(scoreValue));
        
        request.setSemester(ExcelUtils.getCellStringValue(row.getCell(3)));
        
        return request;
    }
}
