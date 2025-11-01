package com.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.dto.ScoreCreateRequest;
import com.student.dto.ScoreResponse;
import com.student.dto.ScoreUpdateRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ScoreService {
    
    /**
     * 创建成绩记录
     */
    ScoreResponse createScore(ScoreCreateRequest request);
    
    /**
     * 更新成绩
     */
    ScoreResponse updateScore(Long id, ScoreUpdateRequest request);
    
    /**
     * 删除成绩记录
     */
    void deleteScore(Long id);
    
    /**
     * 根据ID获取成绩详情
     */
    ScoreResponse getScoreById(Long id);
    
    /**
     * 分页查询成绩列表
     */
    IPage<ScoreResponse> getScoreList(Integer page, Integer size, Long studentId,
                                      Long courseId, String semester, BigDecimal minScore, BigDecimal maxScore);
    
    /**
     * 根据学生ID获取成绩列表
     */
    List<ScoreResponse> getScoresByStudentId(Long studentId);
    
    /**
     * 根据课程ID获取成绩列表
     */
    List<ScoreResponse> getScoresByCourseId(Long courseId);
    
    /**
     * 获取课程成绩统计
     */
    Map<String, Object> getCourseScoreStatistics(Long courseId, String semester);
    
    /**
     * 获取学生成绩统计
     */
    Map<String, Object> getStudentScoreStatistics(Long studentId);
    
    /**
     * 获取班级成绩统计
     */
    List<Map<String, Object>> getClassScoreStatistics(Long classId, String semester);
    
    /**
     * 获取所有学期列表
     */
    List<String> getAllSemesters();
    
    /**
     * 批量删除成绩
     */
    void batchDeleteScores(List<Long> ids);
    
    /**
     * 批量导入成绩
     */
    void batchImportScores(List<ScoreCreateRequest> requests);
}
