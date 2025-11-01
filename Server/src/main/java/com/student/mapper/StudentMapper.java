package com.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.entity.Student;
import com.student.entity.enums.Gender;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学生Mapper接口
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    
    /**
     * 根据学号查询学生
     * @param studentNo 学号
     * @return 学生信息
     */
    @Select("SELECT * FROM student WHERE student_no = #{studentNo}")
    Student findByStudentNo(@Param("studentNo") String studentNo);
    
    /**
     * 根据用户ID查询学生
     * @param userId 用户ID
     * @return 学生信息
     */
    @Select("SELECT * FROM student WHERE user_id = #{userId}")
    Student findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据班级ID查询学生列表
     * @param classId 班级ID
     * @return 学生列表
     */
    @Select("SELECT * FROM student WHERE class_id = #{classId} ORDER BY student_no")
    List<Student> findByClassId(@Param("classId") Long classId);
    
    /**
     * 分页查询学生信息（包含用户和班级信息）
     * @param page 分页参数
     * @param studentNo 学号（可选）
     * @param realName 真实姓名（可选）
     * @param classId 班级ID（可选）
     * @param gender 性别（可选）
     * @return 分页学生信息
     */
    IPage<Student> selectStudentsWithDetails(
            Page<Student> page,
            @Param("studentNo") String studentNo,
            @Param("realName") String realName,
            @Param("classId") Long classId,
            @Param("gender") Gender gender
    );
    
    /**
     * 根据学生ID查询学生详细信息（包含用户和班级信息）
     * @param id 学生ID
     * @return 学生详细信息
     */
    Student selectStudentWithDetails(@Param("id") Long id);
    
    /**
     * 根据班级ID分页查询学生信息（包含用户信息）
     * @param page 分页参数
     * @param classId 班级ID
     * @return 分页学生信息
     */
    IPage<Student> selectStudentsByClassId(Page<Student> page, @Param("classId") Long classId);
    
    /**
     * 查询班级学生数量
     * @param classId 班级ID
     * @return 学生数量
     */
    @Select("SELECT COUNT(*) FROM student WHERE class_id = #{classId}")
    Long countByClassId(@Param("classId") Long classId);
    
    /**
     * 根据性别统计学生数量
     * @param gender 性别
     * @return 学生数量
     */
    @Select("SELECT COUNT(*) FROM student WHERE gender = #{gender}")
    Long countByGender(@Param("gender") Gender gender);
    
    /**
     * 查询没有分配班级的学生
     * @return 学生列表
     */
    @Select("SELECT * FROM student WHERE class_id IS NULL ORDER BY student_no")
    List<Student> findStudentsWithoutClass();
    
    /**
     * 批量更新学生班级
     * @param studentIds 学生ID列表
     * @param classId 班级ID
     * @return 更新数量
     */
    int batchUpdateClassId(@Param("studentIds") List<Long> studentIds, @Param("classId") Long classId);
    
    /**
     * 根据学号模糊查询学生
     * @param studentNo 学号关键字
     * @return 学生列表
     */
    @Select("SELECT * FROM student WHERE student_no LIKE CONCAT('%', #{studentNo}, '%') ORDER BY student_no")
    List<Student> findByStudentNoLike(@Param("studentNo") String studentNo);
    
    /**
     * 查询学生成绩统计信息
     * @param studentId 学生ID
     * @return 成绩统计
     */
    List<Object> selectStudentScoreStatistics(@Param("studentId") Long studentId);
    
    /**
     * 根据班级ID和性别查询学生数量
     * @param classId 班级ID
     * @param gender 性别
     * @return 学生数量
     */
    @Select("SELECT COUNT(*) FROM student WHERE class_id = #{classId} AND gender = #{gender}")
    Long countByClassIdAndGender(@Param("classId") Long classId, @Param("gender") Gender gender);
    
    /**
     * 查询年龄在指定范围内的学生
     * @param minAge 最小年龄
     * @param maxAge 最大年龄
     * @return 学生列表
     */
    List<Student> findStudentsByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);
    
    /**
     * 检查学号是否存在
     * @param studentNo 学号
     * @param excludeId 排除的学生ID（用于更新时检查）
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM student WHERE student_no = #{studentNo} AND (#{excludeId} IS NULL OR id != #{excludeId})")
    boolean existsByStudentNo(@Param("studentNo") String studentNo, @Param("excludeId") Long excludeId);
}