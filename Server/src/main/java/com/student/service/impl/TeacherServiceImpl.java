package com.student.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.common.exception.BusinessException;
import com.student.dto.TeacherCreateRequest;
import com.student.dto.TeacherResponse;
import com.student.dto.TeacherUpdateRequest;
import com.student.entity.Teacher;
import com.student.entity.User;
import com.student.entity.enums.UserRole;
import com.student.mapper.ClassMapper;
import com.student.mapper.CourseMapper;
import com.student.mapper.TeacherMapper;
import com.student.mapper.UserMapper;
import com.student.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl implements TeacherService {
    
    private static final Logger log = LoggerFactory.getLogger(TeacherServiceImpl.class);
    
    @Autowired
    private TeacherMapper teacherMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ClassMapper classMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public TeacherResponse createTeacher(TeacherCreateRequest request) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查教师编号是否已存在
        if (teacherMapper.existsByTeacherNo(request.getTeacherNo(), null)) {
            throw new BusinessException("教师编号已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.TEACHER);
        user.setStatus(true);
        
        userMapper.insert(user);
        log.info("创建教师用户成功: {}", user.getUsername());
        
        // 创建教师
        Teacher teacher = new Teacher();
        teacher.setUserId(user.getId());
        teacher.setTeacherNo(request.getTeacherNo());
        teacher.setDepartment(request.getDepartment());
        teacher.setTitle(request.getTitle());
        
        teacherMapper.insert(teacher);
        log.info("创建教师信息成功: {}", teacher.getTeacherNo());
        
        return getTeacherById(teacher.getId());
    }
    
    @Override
    @Transactional
    public TeacherResponse updateTeacher(Long id, TeacherUpdateRequest request) {
        Teacher teacher = teacherMapper.selectTeacherWithDetails(id);
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }
        
        // 更新用户信息
        User user = userMapper.selectById(teacher.getUserId());
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
        
        // 更新教师信息
        if (request.getDepartment() != null) {
            teacher.setDepartment(request.getDepartment());
        }
        if (request.getTitle() != null) {
            teacher.setTitle(request.getTitle());
        }
        
        teacherMapper.updateById(teacher);
        log.info("更新教师信息成功: {}", teacher.getTeacherNo());
        
        return getTeacherById(id);
    }
    
    @Override
    @Transactional
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }
        
        // 检查是否有班级关联
        List<com.student.entity.Class> classes = classMapper.findByTeacherId(id);
        if (!classes.isEmpty()) {
            throw new BusinessException("该教师还担任班主任，无法删除");
        }
        
        // 删除教师（会级联删除用户）
        teacherMapper.deleteById(id);
        log.info("删除教师成功: {}", teacher.getTeacherNo());
    }
    
    @Override
    public TeacherResponse getTeacherById(Long id) {
        Teacher teacher = teacherMapper.selectTeacherWithDetails(id);
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }
        return convertToResponse(teacher);
    }
    
    @Override
    public TeacherResponse getTeacherByTeacherNo(String teacherNo) {
        Teacher teacher = teacherMapper.findByTeacherNo(teacherNo);
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }
        return getTeacherById(teacher.getId());
    }
    
    @Override
    public IPage<TeacherResponse> getTeacherList(Integer page, Integer size, String teacherNo,
                                                  String realName, String department, String title) {
        Page<Teacher> pageParam = new Page<>(page, size);
        IPage<Teacher> teacherPage = teacherMapper.selectTeachersWithDetails(
                pageParam, teacherNo, realName, department, title);
        
        return teacherPage.convert(this::convertToResponse);
    }
    
    @Override
    public List<TeacherResponse> getTeachersByDepartment(String department) {
        List<Teacher> teachers = teacherMapper.findByDepartment(department);
        return teachers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TeacherResponse> getTeachersByTitle(String title) {
        List<Teacher> teachers = teacherMapper.findByTitle(title);
        return teachers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getAllDepartments() {
        return teacherMapper.findAllDepartments();
    }
    
    @Override
    public List<String> getAllTitles() {
        return teacherMapper.findAllTitles();
    }
    
    @Override
    @Transactional
    public void batchDeleteTeachers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("教师ID列表不能为空");
        }
        
        for (Long id : ids) {
            deleteTeacher(id);
        }
        log.info("批量删除教师成功，共{}个教师", ids.size());
    }
    
    private TeacherResponse convertToResponse(Teacher teacher) {
        TeacherResponse response = new TeacherResponse();
        response.setId(teacher.getId());
        response.setUserId(teacher.getUserId());
        response.setTeacherNo(teacher.getTeacherNo());
        response.setDepartment(teacher.getDepartment());
        response.setTitle(teacher.getTitle());
        response.setCreateTime(teacher.getCreateTime());
        response.setUpdateTime(teacher.getUpdateTime());
        
        if (teacher.getUser() != null) {
            response.setUsername(teacher.getUser().getUsername());
            response.setRealName(teacher.getUser().getRealName());
            response.setEmail(teacher.getUser().getEmail());
            response.setPhone(teacher.getUser().getPhone());
            response.setStatus(teacher.getUser().getStatus());
        }
        
        // 统计班级数量
        List<com.student.entity.Class> classes = classMapper.findByTeacherId(teacher.getId());
        response.setClassCount(classes.size());
        
        // 统计课程数量
        Long courseCount = courseMapper.countByTeacherId(teacher.getId());
        response.setCourseCount(courseCount.intValue());
        
        return response;
    }
}
