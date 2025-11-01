package com.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface ScoreMapper extends BaseMapper<Score> {
    
    /**
     * 分页查询成绩列表（包含学生和课程信息）
     */
    IPage<Score> selectScoresWithDetails(
            Page<Score> page,
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId,
            @Param("semester") String semester,
            @Param("minScore") BigDecimal minScore,
            @Param("maxScore") BigDecimal maxScore
    );
    
    /**
     * 根据ID查询成绩详情（包含学生和课程信息）
     */
    Score selectScoreWithDetails(@Param("id") Long id);
    
    /**
     * 根据课程ID统计选课学生数
     */
    @Select("SELECT COUNT(DISTINCT student_id) FROM score WHERE course_id = #{courseId}")
    Long countStudentsByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 根据课程ID查询成绩列表
     */
    @Select("SELECT * FROM score WHERE course_id = #{courseId} ORDER BY score DESC")
    List<Score> findByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 根据学生ID查询成绩列表
     */
    @Select("SELECT * FROM score WHERE student_id = #{studentId} ORDER BY semester DESC")
    List<Score> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 根据学生ID和课程ID查询成绩
     */
    @Select("SELECT * FROM score WHERE student_id = #{studentId} AND course_id = #{courseId} AND semester = #{semester}")
    Score findByStudentAndCourseAndSemester(@Param("studentId") Long studentId, 
                                            @Param("courseId") Long courseId,
                                            @Param("semester") String semester);
    
    /**
     * 计算学生平均分
     */
    @Select("SELECT AVG(score) FROM score WHERE student_id = #{studentId}")
    BigDecimal calculateAverageByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 计算课程平均分
     */
    @Select("SELECT AVG(score) FROM score WHERE course_id = #{courseId}")
    BigDecimal calculateAverageByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 查询课程成绩统计
     */
    Map<String, Object> getCourseScoreStatistics(@Param("courseId") Long courseId, @Param("semester") String semester);
    
    /**
     * 查询学生成绩统计
     */
    Map<String, Object> getStudentScoreStatistics(@Param("studentId") Long studentId);
    
    /**
     * 查询班级成绩统计
     */
    List<Map<String, Object>> getClassScoreStatistics(@Param("classId") Long classId, @Param("semester") String semester);
    
    /**
     * 查询学期列表
     */
    @Select("SELECT DISTINCT semester FROM score ORDER BY semester DESC")
    List<String> findAllSemesters();
    
    /**
     * 检查成绩是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM score WHERE student_id = #{studentId} AND course_id = #{courseId} AND semester = #{semester} AND (#{excludeId} IS NULL OR id != #{excludeId})")
    boolean existsByStudentAndCourseAndSemester(@Param("studentId") Long studentId,
                                                @Param("courseId") Long courseId,
                                                @Param("semester") String semester,
                                                @Param("excludeId") Long excludeId);
}
