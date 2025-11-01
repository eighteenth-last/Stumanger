package com.student.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.common.exception.BusinessException;
import com.student.dto.ScoreCreateRequest;
import com.student.dto.ScoreResponse;
import com.student.dto.ScoreUpdateRequest;
import com.student.entity.Score;
import com.student.mapper.ScoreMapper;
import com.student.service.ScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScoreServiceImpl implements ScoreService {
    
    private static final Logger log = LoggerFactory.getLogger(ScoreServiceImpl.class);
    
    @Autowired
    private ScoreMapper scoreMapper;
    
    @Override
    @Transactional
    public ScoreResponse createScore(ScoreCreateRequest request) {
        // 检查该学生在该学期是否已有该课程成绩
        if (scoreMapper.existsByStudentAndCourseAndSemester(
                request.getStudentId(), request.getCourseId(), request.getSemester(), null)) {
            throw new BusinessException("该学生在该学期已有该课程成绩记录");
        }
        
        Score score = new Score();
        score.setStudentId(request.getStudentId());
        score.setCourseId(request.getCourseId());
        score.setScore(request.getScore());
        score.setSemester(request.getSemester());
        score.setExamType(request.getExamType());
        score.setExamDate(request.getExamDate());
        
        scoreMapper.insert(score);
        log.info("创建成绩记录成功: 学生ID={}, 课程ID={}, 成绩={}", 
                score.getStudentId(), score.getCourseId(), score.getScore());
        
        return getScoreById(score.getId());
    }
    
    @Override
    @Transactional
    public ScoreResponse updateScore(Long id, ScoreUpdateRequest request) {
        Score score = scoreMapper.selectById(id);
        if (score == null) {
            throw new BusinessException("成绩记录不存在");
        }
        
        score.setScore(request.getScore());
        if (request.getExamType() != null) {
            score.setExamType(request.getExamType());
        }
        if (request.getExamDate() != null) {
            score.setExamDate(request.getExamDate());
        }
        scoreMapper.updateById(score);
        log.info("更新成绩记录成功: ID={}, 新成绩={}", id, request.getScore());
        
        return getScoreById(id);
    }
    
    @Override
    @Transactional
    public void deleteScore(Long id) {
        Score score = scoreMapper.selectById(id);
        if (score == null) {
            throw new BusinessException("成绩记录不存在");
        }
        
        scoreMapper.deleteById(id);
        log.info("删除成绩记录成功: ID={}", id);
    }
    
    @Override
    public ScoreResponse getScoreById(Long id) {
        Score score = scoreMapper.selectScoreWithDetails(id);
        if (score == null) {
            throw new BusinessException("成绩记录不存在");
        }
        return convertToResponse(score);
    }
    
    @Override
    public IPage<ScoreResponse> getScoreList(Integer page, Integer size, Long studentId,
                                             Long courseId, String semester, BigDecimal minScore, BigDecimal maxScore) {
        Page<Score> pageParam = new Page<>(page, size);
        IPage<Score> scorePage = scoreMapper.selectScoresWithDetails(
                pageParam, studentId, courseId, semester, minScore, maxScore);
        
        return scorePage.convert(this::convertToResponse);
    }
    
    @Override
    public List<ScoreResponse> getScoresByStudentId(Long studentId) {
        List<Score> scores = scoreMapper.findByStudentId(studentId);
        return scores.stream()
                .map(score -> {
                    Score detailedScore = scoreMapper.selectScoreWithDetails(score.getId());
                    return convertToResponse(detailedScore);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ScoreResponse> getScoresByCourseId(Long courseId) {
        List<Score> scores = scoreMapper.findByCourseId(courseId);
        return scores.stream()
                .map(score -> {
                    Score detailedScore = scoreMapper.selectScoreWithDetails(score.getId());
                    return convertToResponse(detailedScore);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getCourseScoreStatistics(Long courseId, String semester) {
        return scoreMapper.getCourseScoreStatistics(courseId, semester);
    }
    
    @Override
    public Map<String, Object> getStudentScoreStatistics(Long studentId) {
        return scoreMapper.getStudentScoreStatistics(studentId);
    }
    
    @Override
    public List<Map<String, Object>> getClassScoreStatistics(Long classId, String semester) {
        return scoreMapper.getClassScoreStatistics(classId, semester);
    }
    
    @Override
    public List<String> getAllSemesters() {
        return scoreMapper.findAllSemesters();
    }
    
    @Override
    @Transactional
    public void batchDeleteScores(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("成绩ID列表不能为空");
        }
        
        for (Long id : ids) {
            deleteScore(id);
        }
        log.info("批量删除成绩成功，共{}条记录", ids.size());
    }
    
    @Override
    @Transactional
    public void batchImportScores(List<ScoreCreateRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new BusinessException("导入数据不能为空");
        }
        
        int successCount = 0;
        int failCount = 0;
        
        for (ScoreCreateRequest request : requests) {
            try {
                createScore(request);
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.warn("导入成绩失败: 学生ID={}, 课程ID={}, 原因: {}", 
                        request.getStudentId(), request.getCourseId(), e.getMessage());
            }
        }
        
        log.info("批量导入成绩完成: 成功{}条, 失败{}条", successCount, failCount);
    }
    
    private ScoreResponse convertToResponse(Score score) {
        ScoreResponse response = new ScoreResponse();
        response.setId(score.getId());
        response.setStudentId(score.getStudentId());
        response.setCourseId(score.getCourseId());
        response.setScore(score.getScore());
        response.setSemester(score.getSemester());
        response.setExamType(score.getExamType());
        response.setExamDate(score.getExamDate());
        response.setCreateTime(score.getCreateTime());
        response.setUpdateTime(score.getUpdateTime());
        
        // 设置学生信息
        if (score.getStudent() != null) {
            response.setStudentNo(score.getStudent().getStudentNo());
            if (score.getStudent().getUser() != null) {
                response.setStudentName(score.getStudent().getUser().getRealName());
            }
            if (score.getStudent().getStudentClass() != null) {
                response.setClassName(score.getStudent().getStudentClass().getClassName());
            }
        }
        
        // 设置课程信息
        if (score.getCourse() != null) {
            response.setCourseName(score.getCourse().getCourseName());
            response.setCourseCode(score.getCourse().getCourseCode());
            response.setCredit(score.getCourse().getCredit());
        }
        
        return response;
    }
}
