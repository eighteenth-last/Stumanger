package com.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.entity.Class;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClassMapper extends BaseMapper<Class> {
    
    /**
     * 分页查询班级列表（包含班主任信息）
     */
    IPage<Class> selectClassesWithDetails(
            Page<Class> page,
            @Param("className") String className,
            @Param("major") String major,
            @Param("grade") String grade,
            @Param("enrollYear") Integer enrollYear
    );
    
    /**
     * 根据ID查询班级详情（包含班主任和学生信息）
     */
    Class selectClassWithDetails(@Param("id") Long id);
    
    /**
     * 根据班级名称查询
     */
    @Select("SELECT * FROM class WHERE class_name = #{className}")
    Class findByClassName(@Param("className") String className);
    
    /**
     * 根据专业查询班级列表
     */
    @Select("SELECT * FROM class WHERE major = #{major} ORDER BY grade DESC, class_name")
    List<Class> findByMajor(@Param("major") String major);
    
    /**
     * 根据年级查询班级列表
     */
    @Select("SELECT * FROM class WHERE grade = #{grade} ORDER BY major, class_name")
    List<Class> findByGrade(@Param("grade") String grade);
    
    /**
     * 根据入学年份查询班级列表
     */
    @Select("SELECT * FROM class WHERE enroll_year = #{enrollYear} ORDER BY major, class_name")
    List<Class> findByEnrollYear(@Param("enrollYear") Integer enrollYear);
    
    /**
     * 根据班主任ID查询班级
     */
    @Select("SELECT * FROM class WHERE class_teacher_id = #{teacherId}")
    List<Class> findByTeacherId(@Param("teacherId") Long teacherId);
    
    /**
     * 查询所有专业列表
     */
    @Select("SELECT DISTINCT major FROM class WHERE major IS NOT NULL ORDER BY major")
    List<String> findAllMajors();
    
    /**
     * 查询所有年级列表
     */
    @Select("SELECT DISTINCT grade FROM class WHERE grade IS NOT NULL ORDER BY grade DESC")
    List<String> findAllGrades();
    
    /**
     * 统计班级数量
     */
    @Select("SELECT COUNT(*) FROM class")
    Long countAll();
    
    /**
     * 根据专业统计班级数量
     */
    @Select("SELECT COUNT(*) FROM class WHERE major = #{major}")
    Long countByMajor(@Param("major") String major);
    
    /**
     * 检查班级名称是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM class WHERE class_name = #{className} AND (#{excludeId} IS NULL OR id != #{excludeId})")
    boolean existsByClassName(@Param("className") String className, @Param("excludeId") Long excludeId);
}
