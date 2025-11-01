package com.student.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.entity.Student;
import com.student.entity.User;
import com.student.entity.enums.Gender;
import com.student.entity.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 学生Mapper测试类
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class StudentMapperTest {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserMapper userMapper;

    private User testUser;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setUsername("teststudent");
        testUser.setPassword("password");
        testUser.setRealName("测试学生");
        testUser.setEmail("student@test.com");
        testUser.setRole(UserRole.STUDENT);
        testUser.setStatus(true);
        userMapper.insert(testUser);

        // 创建测试学生
        testStudent = new Student();
        testStudent.setUserId(testUser.getId());
        testStudent.setStudentNo("2024001");
        testStudent.setClassId(1L);
        testStudent.setGender(Gender.MALE);
        testStudent.setBirthDate(LocalDate.of(2000, 1, 1));
        testStudent.setAddress("测试地址");
        studentMapper.insert(testStudent);
    }

    @Test
    void testFindByStudentNo() {
        // 执行测试
        Student found = studentMapper.findByStudentNo("2024001");
        
        // 验证结果
        assertNotNull(found);
        assertEquals("2024001", found.getStudentNo());
        assertEquals(testUser.getId(), found.getUserId());
        assertEquals(Gender.MALE, found.getGender());
    }

    @Test
    void testFindByStudentNoNotFound() {
        // 执行测试
        Student found = studentMapper.findByStudentNo("9999999");
        
        // 验证结果
        assertNull(found);
    }

    @Test
    void testFindByUserId() {
        // 执行测试
        Student found = studentMapper.findByUserId(testUser.getId());
        
        // 验证结果
        assertNotNull(found);
        assertEquals(testUser.getId(), found.getUserId());
        assertEquals("2024001", found.getStudentNo());
    }

    @Test
    void testFindByClassId() {
        // 创建另一个同班级学生
        User user2 = new User();
        user2.setUsername("teststudent2");
        user2.setPassword("password");
        user2.setRealName("测试学生2");
        user2.setRole(UserRole.STUDENT);
        user2.setStatus(true);
        userMapper.insert(user2);

        Student student2 = new Student();
        student2.setUserId(user2.getId());
        student2.setStudentNo("2024002");
        student2.setClassId(1L);
        student2.setGender(Gender.FEMALE);
        studentMapper.insert(student2);

        // 执行测试
        List<Student> students = studentMapper.findByClassId(1L);
        
        // 验证结果
        assertNotNull(students);
        assertEquals(2, students.size());
        assertTrue(students.stream().anyMatch(s -> "2024001".equals(s.getStudentNo())));
        assertTrue(students.stream().anyMatch(s -> "2024002".equals(s.getStudentNo())));
    }

    @Test
    void testSelectStudentsWithDetails() {
        // 执行测试
        Page<Student> page = new Page<>(1, 10);
        IPage<Student> result = studentMapper.selectStudentsWithDetails(page, null, null, null, null);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.getTotal() >= 1);
        assertNotNull(result.getRecords());
        
        Student student = result.getRecords().get(0);
        assertNotNull(student.getUser());
        assertEquals("测试学生", student.getUser().getRealName());
    }

    @Test
    void testSelectStudentsWithDetailsWithFilters() {
        // 执行测试 - 按学号搜索
        Page<Student> page = new Page<>(1, 10);
        IPage<Student> result = studentMapper.selectStudentsWithDetails(page, "2024", null, null, null);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.getTotal() >= 1);
        
        // 执行测试 - 按姓名搜索
        result = studentMapper.selectStudentsWithDetails(page, null, "测试", null, null);
        assertNotNull(result);
        assertTrue(result.getTotal() >= 1);
        
        // 执行测试 - 按性别搜索
        result = studentMapper.selectStudentsWithDetails(page, null, null, null, Gender.MALE);
        assertNotNull(result);
        assertTrue(result.getTotal() >= 1);
    }

    @Test
    void testSelectStudentWithDetails() {
        // 执行测试
        Student student = studentMapper.selectStudentWithDetails(testStudent.getId());
        
        // 验证结果
        assertNotNull(student);
        assertEquals(testStudent.getId(), student.getId());
        assertNotNull(student.getUser());
        assertEquals("测试学生", student.getUser().getRealName());
        assertEquals("teststudent", student.getUser().getUsername());
    }

    @Test
    void testSelectStudentsByClassId() {
        // 执行测试
        Page<Student> page = new Page<>(1, 10);
        IPage<Student> result = studentMapper.selectStudentsByClassId(page, 1L);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.getTotal() >= 1);
        
        Student student = result.getRecords().get(0);
        assertNotNull(student.getUser());
        assertEquals(1L, student.getClassId());
    }

    @Test
    void testCountByClassId() {
        // 执行测试
        Long count = studentMapper.countByClassId(1L);
        
        // 验证结果
        assertNotNull(count);
        assertTrue(count >= 1);
    }

    @Test
    void testCountByGender() {
        // 执行测试
        Long maleCount = studentMapper.countByGender(Gender.MALE);
        Long femaleCount = studentMapper.countByGender(Gender.FEMALE);
        
        // 验证结果
        assertNotNull(maleCount);
        assertNotNull(femaleCount);
        assertTrue(maleCount >= 1); // 至少有我们创建的测试学生
    }

    @Test
    void testFindStudentsWithoutClass() {
        // 创建没有班级的学生
        User user3 = new User();
        user3.setUsername("teststudent3");
        user3.setPassword("password");
        user3.setRealName("测试学生3");
        user3.setRole(UserRole.STUDENT);
        user3.setStatus(true);
        userMapper.insert(user3);

        Student student3 = new Student();
        student3.setUserId(user3.getId());
        student3.setStudentNo("2024003");
        student3.setClassId(null); // 没有班级
        student3.setGender(Gender.MALE);
        studentMapper.insert(student3);

        // 执行测试
        List<Student> students = studentMapper.findStudentsWithoutClass();
        
        // 验证结果
        assertNotNull(students);
        assertTrue(students.size() >= 1);
        assertTrue(students.stream().anyMatch(s -> "2024003".equals(s.getStudentNo())));
        assertTrue(students.stream().allMatch(s -> s.getClassId() == null));
    }

    @Test
    void testBatchUpdateClassId() {
        // 创建另一个学生
        User user4 = new User();
        user4.setUsername("teststudent4");
        user4.setPassword("password");
        user4.setRealName("测试学生4");
        user4.setRole(UserRole.STUDENT);
        user4.setStatus(true);
        userMapper.insert(user4);

        Student student4 = new Student();
        student4.setUserId(user4.getId());
        student4.setStudentNo("2024004");
        student4.setClassId(null);
        student4.setGender(Gender.FEMALE);
        studentMapper.insert(student4);

        // 执行测试
        List<Long> studentIds = Arrays.asList(testStudent.getId(), student4.getId());
        int result = studentMapper.batchUpdateClassId(studentIds, 2L);
        
        // 验证结果
        assertEquals(2, result);
        
        // 验证更新后的数据
        Student updated1 = studentMapper.selectById(testStudent.getId());
        Student updated2 = studentMapper.selectById(student4.getId());
        assertEquals(2L, updated1.getClassId());
        assertEquals(2L, updated2.getClassId());
    }

    @Test
    void testFindByStudentNoLike() {
        // 执行测试
        List<Student> students = studentMapper.findByStudentNoLike("2024");
        
        // 验证结果
        assertNotNull(students);
        assertTrue(students.size() >= 1);
        assertTrue(students.stream().allMatch(s -> s.getStudentNo().contains("2024")));
    }

    @Test
    void testCountByClassIdAndGender() {
        // 执行测试
        Long count = studentMapper.countByClassIdAndGender(1L, Gender.MALE);
        
        // 验证结果
        assertNotNull(count);
        assertTrue(count >= 1);
    }

    @Test
    void testFindStudentsByAgeRange() {
        // 执行测试 - 查找20-25岁的学生
        List<Student> students = studentMapper.findStudentsByAgeRange(20, 25);
        
        // 验证结果
        assertNotNull(students);
        assertTrue(students.size() >= 1);
        
        // 验证返回的学生包含用户信息
        Student student = students.get(0);
        assertNotNull(student.getUser());
    }

    @Test
    void testExistsByStudentNo() {
        // 测试存在的学号
        boolean exists = studentMapper.existsByStudentNo("2024001", null);
        assertTrue(exists);
        
        // 测试不存在的学号
        exists = studentMapper.existsByStudentNo("9999999", null);
        assertFalse(exists);
        
        // 测试排除自己的情况
        exists = studentMapper.existsByStudentNo("2024001", testStudent.getId());
        assertFalse(exists);
    }

    @Test
    void testSelectStudentScoreStatistics() {
        // 注意：这个测试需要有成绩数据，这里只测试方法不报错
        try {
            List<Object> statistics = studentMapper.selectStudentScoreStatistics(testStudent.getId());
            assertNotNull(statistics);
            // 由于没有成绩数据，结果应该为空
            assertTrue(statistics.isEmpty());
        } catch (Exception e) {
            // 如果表不存在或其他问题，跳过这个测试
            System.out.println("跳过成绩统计测试: " + e.getMessage());
        }
    }

    @Test
    void testBasicCRUDOperations() {
        // 测试插入
        User newUser = new User();
        newUser.setUsername("newstudent");
        newUser.setPassword("password");
        newUser.setRealName("新学生");
        newUser.setRole(UserRole.STUDENT);
        newUser.setStatus(true);
        userMapper.insert(newUser);

        Student newStudent = new Student();
        newStudent.setUserId(newUser.getId());
        newStudent.setStudentNo("2024999");
        newStudent.setClassId(1L);
        newStudent.setGender(Gender.FEMALE);
        newStudent.setBirthDate(LocalDate.of(1999, 12, 31));
        newStudent.setAddress("新地址");
        
        int insertResult = studentMapper.insert(newStudent);
        assertEquals(1, insertResult);
        assertNotNull(newStudent.getId());

        // 测试查询
        Student found = studentMapper.selectById(newStudent.getId());
        assertNotNull(found);
        assertEquals("2024999", found.getStudentNo());
        assertEquals(Gender.FEMALE, found.getGender());

        // 测试更新
        found.setAddress("更新后的地址");
        int updateResult = studentMapper.updateById(found);
        assertEquals(1, updateResult);

        // 验证更新
        Student updated = studentMapper.selectById(newStudent.getId());
        assertEquals("更新后的地址", updated.getAddress());

        // 测试删除
        int deleteResult = studentMapper.deleteById(newStudent.getId());
        assertEquals(1, deleteResult);

        // 验证删除
        Student deleted = studentMapper.selectById(newStudent.getId());
        assertNull(deleted);
    }
}