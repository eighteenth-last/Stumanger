package com.student.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.common.exception.BusinessException;
import com.student.dto.StudentCreateRequest;
import com.student.dto.StudentResponse;
import com.student.dto.StudentUpdateRequest;
import com.student.entity.Student;
import com.student.entity.User;
import com.student.entity.enums.Gender;
import com.student.entity.enums.UserRole;
import com.student.mapper.StudentMapper;
import com.student.mapper.UserMapper;
import com.student.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {
    
    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);
    
    @Autowired
    private StudentMapper studentMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public StudentResponse createStudent(StudentCreateRequest request) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查学号是否已存在
        if (studentMapper.existsByStudentNo(request.getStudentNo(), null)) {
            throw new BusinessException("学号已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.STUDENT);
        user.setStatus(true);
        
        userMapper.insert(user);
        log.info("创建学生用户成功: {}", user.getUsername());
        
        // 创建学生
        Student student = new Student();
        student.setUserId(user.getId());
        student.setStudentNo(request.getStudentNo());
        student.setClassId(request.getClassId());
        student.setGender(request.getGender());
        student.setBirthDate(request.getBirthDate());
        student.setAddress(request.getAddress());
        
        studentMapper.insert(student);
        log.info("创建学生信息成功: {}", student.getStudentNo());
        
        return getStudentById(student.getId());
    }
    
    @Override
    @Transactional
    public StudentResponse updateStudent(Long id, StudentUpdateRequest request) {
        Student student = studentMapper.selectStudentWithDetails(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        // 更新用户信息
        User user = userMapper.selectById(student.getUserId());
        if (user != null) {
            if (request.getRealName() != null) {
                user.setRealName(request.getRealName());
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
            if (request.getPhone() != null) {
                user.setPhone(request.getPhone());
            }
            if (request.getStatus() != null) {
                user.setStatus(request.getStatus());
            }
            userMapper.updateById(user);
        }
        
        // 更新学生信息
        if (request.getClassId() != null) {
            student.setClassId(request.getClassId());
        }
        if (request.getGender() != null) {
            student.setGender(request.getGender());
        }
        if (request.getBirthDate() != null) {
            student.setBirthDate(request.getBirthDate());
        }
        if (request.getAddress() != null) {
            student.setAddress(request.getAddress());
        }
        
        studentMapper.updateById(student);
        log.info("更新学生信息成功: {}", student.getStudentNo());
        
        return getStudentById(id);
    }
    
    @Override
    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        // 删除学生（会级联删除用户）
        studentMapper.deleteById(id);
        log.info("删除学生成功: {}", student.getStudentNo());
    }
    
    @Override
    public StudentResponse getStudentById(Long id) {
        Student student = studentMapper.selectStudentWithDetails(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        return convertToResponse(student);
    }
    
    @Override
    public StudentResponse getStudentByStudentNo(String studentNo) {
        Student student = studentMapper.findByStudentNo(studentNo);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        return getStudentById(student.getId());
    }
    
    @Override
    public IPage<StudentResponse> getStudentList(Integer page, Integer size, String studentNo,
                                                  String realName, Long classId, Gender gender) {
        Page<Student> pageParam = new Page<>(page, size);
        IPage<Student> studentPage = studentMapper.selectStudentsWithDetails(
                pageParam, studentNo, realName, classId, gender);
        
        return studentPage.convert(this::convertToResponse);
    }
    
    @Override
    public List<StudentResponse> getStudentsByClassId(Long classId) {
        List<Student> students = studentMapper.findByClassId(classId);
        return students.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void batchAssignClass(List<Long> studentIds, Long classId) {
        if (studentIds == null || studentIds.isEmpty()) {
            throw new BusinessException("学生ID列表不能为空");
        }
        
        int count = studentMapper.batchUpdateClassId(studentIds, classId);
        log.info("批量分配班级成功，共{}个学生", count);
    }
    
    @Override
    public List<StudentResponse> getStudentsWithoutClass() {
        List<Student> students = studentMapper.findStudentsWithoutClass();
        return students.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void batchDeleteStudents(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("学生ID列表不能为空");
        }
        
        for (Long id : ids) {
            deleteStudent(id);
        }
        log.info("批量删除学生成功，共{}个学生", ids.size());
    }
    
    private StudentResponse convertToResponse(Student student) {
        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setUserId(student.getUserId());
        response.setStudentNo(student.getStudentNo());
        response.setClassId(student.getClassId());
        response.setGender(student.getGender());
        response.setBirthDate(student.getBirthDate());
        response.setAddress(student.getAddress());
        response.setCreateTime(student.getCreateTime());
        response.setUpdateTime(student.getUpdateTime());
        
        if (student.getUser() != null) {
            response.setUsername(student.getUser().getUsername());
            response.setRealName(student.getUser().getRealName());
            response.setEmail(student.getUser().getEmail());
            response.setPhone(student.getUser().getPhone());
            response.setStatus(student.getUser().getStatus());
        }
        
        if (student.getStudentClass() != null) {
            response.setClassName(student.getStudentClass().getClassName());
            response.setMajor(student.getStudentClass().getMajor());
            response.setGrade(student.getStudentClass().getGrade());
        }
        
        return response;
    }
}
