package com.student.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.common.exception.BusinessException;
import com.student.dto.ClassCreateRequest;
import com.student.dto.ClassResponse;
import com.student.dto.ClassUpdateRequest;
import com.student.entity.Class;
import com.student.mapper.ClassMapper;
import com.student.mapper.StudentMapper;
import com.student.service.ClassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassServiceImpl implements ClassService {
    
    private static final Logger log = LoggerFactory.getLogger(ClassServiceImpl.class);
    
    @Autowired
    private ClassMapper classMapper;
    
    @Autowired
    private StudentMapper studentMapper;
    
    @Override
    @Transactional
    public ClassResponse createClass(ClassCreateRequest request) {
        // 检查班级名称是否已存在
        if (classMapper.existsByClassName(request.getClassName(), null)) {
            throw new BusinessException("班级名称已存在");
        }
        
        Class classEntity = new Class();
        classEntity.setClassName(request.getClassName());
        classEntity.setMajor(request.getMajor());
        classEntity.setGrade(request.getGrade());
        classEntity.setEnrollYear(request.getEnrollYear());
        classEntity.setClassTeacherId(request.getClassTeacherId());
        
        classMapper.insert(classEntity);
        log.info("创建班级成功: {}", classEntity.getClassName());
        
        return getClassById(classEntity.getId());
    }
    
    @Override
    @Transactional
    public ClassResponse updateClass(Long id, ClassUpdateRequest request) {
        Class classEntity = classMapper.selectById(id);
        if (classEntity == null) {
            throw new BusinessException("班级不存在");
        }
        
        // 如果修改了班级名称，检查新名称是否已存在
        if (request.getClassName() != null && !request.getClassName().equals(classEntity.getClassName())) {
            if (classMapper.existsByClassName(request.getClassName(), id)) {
                throw new BusinessException("班级名称已存在");
            }
            classEntity.setClassName(request.getClassName());
        }
        
        if (request.getMajor() != null) {
            classEntity.setMajor(request.getMajor());
        }
        if (request.getGrade() != null) {
            classEntity.setGrade(request.getGrade());
        }
        if (request.getEnrollYear() != null) {
            classEntity.setEnrollYear(request.getEnrollYear());
        }
        if (request.getClassTeacherId() != null) {
            classEntity.setClassTeacherId(request.getClassTeacherId());
        }
        
        classMapper.updateById(classEntity);
        log.info("更新班级信息成功: {}", classEntity.getClassName());
        
        return getClassById(id);
    }
    
    @Override
    @Transactional
    public void deleteClass(Long id) {
        Class classEntity = classMapper.selectById(id);
        if (classEntity == null) {
            throw new BusinessException("班级不存在");
        }
        
        // 检查班级是否有学生
        Long studentCount = studentMapper.countByClassId(id);
        if (studentCount > 0) {
            throw new BusinessException("该班级还有学生，无法删除");
        }
        
        classMapper.deleteById(id);
        log.info("删除班级成功: {}", classEntity.getClassName());
    }
    
    @Override
    public ClassResponse getClassById(Long id) {
        Class classEntity = classMapper.selectClassWithDetails(id);
        if (classEntity == null) {
            throw new BusinessException("班级不存在");
        }
        return convertToResponse(classEntity);
    }
    
    @Override
    public IPage<ClassResponse> getClassList(Integer page, Integer size, String className,
                                             String major, String grade, Integer enrollYear) {
        Page<Class> pageParam = new Page<>(page, size);
        IPage<Class> classPage = classMapper.selectClassesWithDetails(
                pageParam, className, major, grade, enrollYear);
        
        return classPage.convert(this::convertToResponse);
    }
    
    @Override
    public List<ClassResponse> getClassesByMajor(String major) {
        List<Class> classes = classMapper.findByMajor(major);
        return classes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ClassResponse> getClassesByGrade(String grade) {
        List<Class> classes = classMapper.findByGrade(grade);
        return classes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getAllMajors() {
        return classMapper.findAllMajors();
    }
    
    @Override
    public List<String> getAllGrades() {
        return classMapper.findAllGrades();
    }
    
    @Override
    @Transactional
    public void batchDeleteClasses(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("班级ID列表不能为空");
        }
        
        for (Long id : ids) {
            deleteClass(id);
        }
        log.info("批量删除班级成功，共{}个班级", ids.size());
    }
    
    private ClassResponse convertToResponse(Class classEntity) {
        ClassResponse response = new ClassResponse();
        response.setId(classEntity.getId());
        response.setClassName(classEntity.getClassName());
        response.setMajor(classEntity.getMajor());
        response.setGrade(classEntity.getGrade());
        response.setEnrollYear(classEntity.getEnrollYear());
        response.setClassTeacherId(classEntity.getClassTeacherId());
        response.setCreateTime(classEntity.getCreateTime());
        response.setUpdateTime(classEntity.getUpdateTime());
        
        // 获取学生数量
        Long studentCount = studentMapper.countByClassId(classEntity.getId());
        response.setStudentCount(studentCount.intValue());
        
        // 设置班主任信息
        if (classEntity.getClassTeacher() != null) {
            response.setClassTeacherNo(classEntity.getClassTeacher().getTeacherNo());
            if (classEntity.getClassTeacher().getUser() != null) {
                response.setClassTeacherName(classEntity.getClassTeacher().getUser().getRealName());
            }
        }
        
        return response;
    }
}
