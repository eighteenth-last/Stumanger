package com.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.dto.ClassCreateRequest;
import com.student.dto.ClassResponse;
import com.student.dto.ClassUpdateRequest;

import java.util.List;

public interface ClassService {
    
    /**
     * 创建班级
     */
    ClassResponse createClass(ClassCreateRequest request);
    
    /**
     * 更新班级信息
     */
    ClassResponse updateClass(Long id, ClassUpdateRequest request);
    
    /**
     * 删除班级
     */
    void deleteClass(Long id);
    
    /**
     * 根据ID获取班级详情
     */
    ClassResponse getClassById(Long id);
    
    /**
     * 分页查询班级列表
     */
    IPage<ClassResponse> getClassList(Integer page, Integer size, String className,
                                      String major, String grade, Integer enrollYear);
    
    /**
     * 根据专业获取班级列表
     */
    List<ClassResponse> getClassesByMajor(String major);
    
    /**
     * 根据年级获取班级列表
     */
    List<ClassResponse> getClassesByGrade(String grade);
    
    /**
     * 获取所有专业列表
     */
    List<String> getAllMajors();
    
    /**
     * 获取所有年级列表
     */
    List<String> getAllGrades();
    
    /**
     * 批量删除班级
     */
    void batchDeleteClasses(List<Long> ids);
}
