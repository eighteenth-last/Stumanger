package com.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
    
    /**
     * 分页查询教师列表（包含用户信息）
     */
    IPage<Teacher> selectTeachersWithDetails(
            Page<Teacher> page,
            @Param("teacherNo") String teacherNo,
            @Param("realName") String realName,
            @Param("department") String department,
            @Param("title") String title
    );
    
    /**
     * 根据ID查询教师详情（包含用户信息）
     */
    Teacher selectTeacherWithDetails(@Param("id") Long id);
    
    /**
     * 根据教师编号查询
     */
    @Select("SELECT * FROM teacher WHERE teacher_no = #{teacherNo}")
    Teacher findByTeacherNo(@Param("teacherNo") String teacherNo);
    
    /**
     * 根据用户ID查询教师
     */
    @Select("SELECT * FROM teacher WHERE user_id = #{userId}")
    Teacher findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据部门查询教师列表
     */
    @Select("SELECT * FROM teacher WHERE department = #{department} ORDER BY teacher_no")
    List<Teacher> findByDepartment(@Param("department") String department);
    
    /**
     * 根据职称查询教师列表
     */
    @Select("SELECT * FROM teacher WHERE title = #{title} ORDER BY teacher_no")
    List<Teacher> findByTitle(@Param("title") String title);
    
    /**
     * 查询所有部门列表
     */
    @Select("SELECT DISTINCT department FROM teacher WHERE department IS NOT NULL ORDER BY department")
    List<String> findAllDepartments();
    
    /**
     * 查询所有职称列表
     */
    @Select("SELECT DISTINCT title FROM teacher WHERE title IS NOT NULL ORDER BY title")
    List<String> findAllTitles();
    
    /**
     * 统计教师数量
     */
    @Select("SELECT COUNT(*) FROM teacher")
    Long countAll();
    
    /**
     * 根据部门统计教师数量
     */
    @Select("SELECT COUNT(*) FROM teacher WHERE department = #{department}")
    Long countByDepartment(@Param("department") String department);
    
    /**
     * 根据职称统计教师数量
     */
    @Select("SELECT COUNT(*) FROM teacher WHERE title = #{title}")
    Long countByTitle(@Param("title") String title);
    
    /**
     * 检查教师编号是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM teacher WHERE teacher_no = #{teacherNo} AND (#{excludeId} IS NULL OR id != #{excludeId})")
    boolean existsByTeacherNo(@Param("teacherNo") String teacherNo, @Param("excludeId") Long excludeId);
    
    /**
     * 根据教师编号模糊查询
     */
    @Select("SELECT * FROM teacher WHERE teacher_no LIKE CONCAT('%', #{teacherNo}, '%') ORDER BY teacher_no")
    List<Teacher> findByTeacherNoLike(@Param("teacherNo") String teacherNo);
}
