package com.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    
    /**
     * 分页查询课程列表（包含教师信息）
     */
    IPage<Course> selectCoursesWithDetails(
            Page<Course> page,
            @Param("courseName") String courseName,
            @Param("courseCode") String courseCode,
            @Param("teacherId") Long teacherId
    );
    
    /**
     * 根据ID查询课程详情（包含教师信息）
     */
    Course selectCourseWithDetails(@Param("id") Long id);
    
    /**
     * 根据课程代码查询
     */
    @Select("SELECT * FROM course WHERE course_code = #{courseCode}")
    Course findByCourseCode(@Param("courseCode") String courseCode);
    
    /**
     * 根据教师ID查询课程列表
     */
    @Select("SELECT * FROM course WHERE teacher_id = #{teacherId} ORDER BY course_code")
    List<Course> findByTeacherId(@Param("teacherId") Long teacherId);
    
    /**
     * 根据课程名称模糊查询
     */
    @Select("SELECT * FROM course WHERE course_name LIKE CONCAT('%', #{courseName}, '%') ORDER BY course_code")
    List<Course> findByCourseNameLike(@Param("courseName") String courseName);
    
    /**
     * 统计课程数量
     */
    @Select("SELECT COUNT(*) FROM course")
    Long countAll();
    
    /**
     * 根据教师ID统计课程数量
     */
    @Select("SELECT COUNT(*) FROM course WHERE teacher_id = #{teacherId}")
    Long countByTeacherId(@Param("teacherId") Long teacherId);
    
    /**
     * 检查课程代码是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM course WHERE course_code = #{courseCode} AND (#{excludeId} IS NULL OR id != #{excludeId})")
    boolean existsByCourseCode(@Param("courseCode") String courseCode, @Param("excludeId") Long excludeId);
    
    /**
     * 查询学分范围内的课程
     */
    @Select("SELECT * FROM course WHERE credit BETWEEN #{minCredit} AND #{maxCredit} ORDER BY credit, course_code")
    List<Course> findByCreditRange(@Param("minCredit") Double minCredit, @Param("maxCredit") Double maxCredit);
}
