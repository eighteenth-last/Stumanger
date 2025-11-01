/*
 Navicat Premium Dump SQL

 Source Server         : MySQL-Pro
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : student_management

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 02/11/2025 00:59:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for class
-- ----------------------------
DROP TABLE IF EXISTS `class`;
CREATE TABLE `class`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '班级名称',
  `major` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业',
  `grade` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年级',
  `enroll_year` int NULL DEFAULT NULL COMMENT '入学年份',
  `class_teacher_id` bigint NULL DEFAULT NULL COMMENT '班主任ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_class_teacher_id`(`class_teacher_id` ASC) USING BTREE,
  INDEX `idx_class_grade`(`grade` ASC) USING BTREE,
  INDEX `idx_class_major`(`major` ASC) USING BTREE,
  INDEX `idx_class_enroll_year`(`enroll_year` ASC) USING BTREE,
  CONSTRAINT `class_ibfk_1` FOREIGN KEY (`class_teacher_id`) REFERENCES `teacher` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '班级表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of class
-- ----------------------------
INSERT INTO `class` VALUES (1, '计科2101', '计算机科学与技术', '2021', 2021, 1, '2025-10-09 15:04:32', '2025-10-09 15:04:32');
INSERT INTO `class` VALUES (2, '数学2101', '信息与计算科学', '2021', 2021, 2, '2025-10-09 15:04:32', '2025-10-09 15:04:32');
INSERT INTO `class` VALUES (3, '英语2101', '英语', '2021', 2021, 3, '2025-10-09 15:04:32', '2025-10-09 15:04:32');
INSERT INTO `class` VALUES (4, '计科2101', '计算机科学与技术', '2021', 2029, 1, '2025-10-09 15:10:28', '2025-10-09 15:10:28');
INSERT INTO `class` VALUES (5, '数学2101', '信息与计算科学', '2021', 2021, 2, '2025-10-09 15:10:28', '2025-10-09 15:10:28');
INSERT INTO `class` VALUES (6, '英语2101', '英语', '2021', 2021, 3, '2025-10-09 15:10:28', '2025-10-09 15:10:28');
INSERT INTO `class` VALUES (10, 'Ai全栈班', '应用开发', '大一', 2023, 5, '2025-11-01 22:44:08', '2025-11-01 22:44:08');

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `course_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程代码',
  `credit` decimal(3, 1) NOT NULL COMMENT '学分',
  `hours` int NULL DEFAULT NULL COMMENT '学时',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学期',
  `classroom` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '教室',
  `class_time` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上课时间',
  `teacher_id` bigint NOT NULL COMMENT '授课教师ID',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '课程描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_course_code`(`course_code` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_course_name`(`course_name` ASC) USING BTREE,
  INDEX `idx_course_credit`(`credit` ASC) USING BTREE,
  CONSTRAINT `course_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, '离散数学', 'MA101', 3.0, 48, '2025-2026-1', '201', '周二 1-2节, 周四 3-4节', 2, '数理逻辑、集合论、图论基础', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `course` VALUES (2, 'Java程序设计', 'CS101', 4.0, 2, '2025-2026第一学期', '302', '周一 1-2节', 1, 'Java语法、面向对象、集合框架', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `course` VALUES (3, '英语写作', 'EN101', 2.0, 32, '2023-2024-1', '教学楼C102', '周五 1-2节', 3, '学术英语写作技巧', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `course` VALUES (4, '数据结构', 'CS102', 4.0, 64, '2023-2024-2', '实验楼B202', '周一 3-4节, 周三 1-2节', 1, '线性表、树、图及算法', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `course` VALUES (5, '高等数学A', 'MA201', 5.0, 80, '2023-2024-1', '教学楼A201', '周一 1-2节, 周三 3-4节, 周五 1节', 2, '微积分、级数、微分方程', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `course` VALUES (6, '操作系统', 'CS201', 4.0, 64, '2023-2024-2', '实验楼B203', '周二 1-2节, 周四 3-4节', 1, '进程、内存、文件系统', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `course` VALUES (7, '线性代数', 'MA102', 3.0, 48, '2023-2024-2', '教学楼A302', '周二 3-4节, 周四 1-2节', 2, '矩阵、向量空间、特征值', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `course` VALUES (8, '英语口语', 'EN201', 2.0, 32, '2023-2024-2', '教学楼C103', '周三 3-4节', 3, '情景对话与演讲训练', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `course` VALUES (9, '机器学习导论', 'CS301', 3.0, 48, '2024-2025-1', '实验楼B301', '周一 3-4节, 周三 1-2节', 1, '监督/无监督学习基础', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `course` VALUES (10, '毕业论文', 'CS401', 6.0, 96, '2024-2025-2', '各导师办公室', '预约制', 1, '毕业设计与论文撰写', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `course` VALUES (11, 'Ai全栈开发', 'CS402', 10.0, 200, '2024-2025-1', '荣英楼 101', '周一1-10节', 5, '学习全栈开发', '2025-11-01 22:39:01', '2025-11-01 22:39:01');

-- ----------------------------
-- Table structure for score
-- ----------------------------
DROP TABLE IF EXISTS `score`;
CREATE TABLE `score`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '成绩ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `score` decimal(5, 2) NOT NULL COMMENT '成绩',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学期',
  `exam_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '考试类型',
  `exam_date` date NULL DEFAULT NULL COMMENT '考试日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_course_semester`(`student_id` ASC, `course_id` ASC, `semester` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_score_semester`(`semester` ASC) USING BTREE,
  INDEX `idx_score_value`(`score` ASC) USING BTREE,
  INDEX `idx_score_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `score_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `score_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 91 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '成绩表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of score
-- ----------------------------
INSERT INTO `score` VALUES (61, 5, 1, 92.50, '2023-2024-1', '期末考试', '2024-01-15', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (62, 5, 2, 88.00, '2023-2024-1', '期末考试', '2024-01-18', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (63, 5, 4, 90.50, '2023-2024-2', '期末考试', '2024-06-20', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (64, 6, 3, 85.00, '2023-2024-1', '期末考试', '2024-01-12', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (65, 6, 5, 91.00, '2023-2024-1', '期末考试', '2024-01-16', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (66, 6, 7, 87.50, '2023-2024-2', '期末考试', '2024-06-18', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (67, 7, 2, 93.00, '2023-2024-1', '期末考试', '2024-01-18', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (68, 7, 4, 89.00, '2023-2024-2', '期末考试', '2024-06-20', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (69, 7, 9, 95.00, '2023-2024-2', '期末考试', '2024-06-25', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (70, 8, 1, 78.00, '2023-2024-1', '期中考试', '2023-11-20', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (71, 8, 5, 82.50, '2023-2024-1', '期末考试', '2024-01-16', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (72, 8, 6, 85.50, '2023-2024-2', '期末考试', '2024-06-22', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (73, 9, 2, 94.00, '2023-2024-1', '期末考试', '2024-01-18', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (74, 9, 4, 91.50, '2023-2024-2', '期末考试', '2024-06-20', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (75, 9, 10, 50.00, '2023-2024-2', '重修', '2025-11-06', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (76, 10, 3, 80.00, '2023-2024-1', '期末考试', '2024-01-12', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (77, 10, 7, 88.00, '2023-2024-1', '期末考试', '2024-01-19', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (78, 10, 8, 84.00, '2023-2024-2', '期末考试', '2024-06-19', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (79, 11, 5, 90.00, '2023-2024-1', '期末考试', '2024-01-16', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (80, 11, 7, 92.50, '2023-2024-2', '期末考试', '2024-06-18', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (81, 11, 8, 87.00, '2023-2024-2', '期末考试', '2024-06-19', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (82, 12, 3, 86.00, '2023-2024-1', '期末考试', '2024-01-12', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (83, 12, 8, 89.50, '2023-2024-1', '期中考试', '2023-11-25', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (84, 12, 9, 93.00, '2023-2024-2', '期末考试', '2024-06-25', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (85, 13, 6, 88.00, '2023-2024-2', '期末考试', '2024-06-22', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (86, 13, 9, 91.00, '2023-2024-2', '期末考试', '2024-06-25', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (87, 13, 10, 94.50, '2023-2024-2', '答辩', '2024-06-28', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (88, 14, 1, 81.00, '2023-2024-1', '期末考试', '2024-01-15', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (89, 14, 2, 87.50, '2023-2024-1', '期末考试', '2024-01-18', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `score` VALUES (90, 14, 4, 85.00, '2023-2024-2', '期末考试', '2024-06-20', '2025-11-01 19:48:26', '2025-11-01 19:48:26');

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `student_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学号',
  `class_id` bigint NULL DEFAULT NULL COMMENT '班级ID',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别(0:女,1:男)',
  `birth_date` date NULL DEFAULT NULL COMMENT '出生日期',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_no`(`student_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_class_id`(`class_id` ASC) USING BTREE,
  INDEX `idx_student_gender`(`gender` ASC) USING BTREE,
  INDEX `idx_student_birth_date`(`birth_date` ASC) USING BTREE,
  CONSTRAINT `student_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `student_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES (5, 5, '2021210801', 1, 1, '2003-05-18', '湖北省武汉市', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `student` VALUES (6, 6, '2021210802', 1, 0, '2002-11-30', '广东省深圳市', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `student` VALUES (7, 7, '2021210803', 2, 1, '2003-02-14', '浙江省杭州市', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `student` VALUES (8, 8, '2021210804', 2, 0, '2003-07-07', '江苏省南京市', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `student` VALUES (9, 9, '2021210805', 3, 1, '2002-12-25', '四川省成都市', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `student` VALUES (10, 10, '2021210806', 3, 0, '2003-04-10', '山东省青岛市', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `student` VALUES (11, 11, '2021210807', 1, 1, '2003-08-08', '河南省郑州市', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `student` VALUES (12, 12, '2021210808', 2, 0, '2003-09-09', '湖南省长沙市', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `student` VALUES (13, 13, '2021210809', 3, 1, '2003-10-10', '福建省厦门市', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `student` VALUES (14, 14, '2021210810', 1, 0, '2003-11-11', '江西省南昌市', '2025-11-01 19:48:26', '2025-11-01 19:48:26');
INSERT INTO `student` VALUES (15, 16, '2023900115', 10, 1, '2025-11-04', '暂无', '2025-11-01 22:42:46', '2025-11-01 22:42:46');

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `teacher_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师编号',
  `department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属部门',
  `title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_teacher_no`(`teacher_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_teacher_department`(`department` ASC) USING BTREE,
  INDEX `idx_teacher_title`(`title` ASC) USING BTREE,
  CONSTRAINT `teacher_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教师表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of teacher
-- ----------------------------
INSERT INTO `teacher` VALUES (1, 2, 'T2021001', '计算机系', '副教授', '2025-10-09 15:03:25', '2025-10-09 15:03:25');
INSERT INTO `teacher` VALUES (2, 3, 'T2021002', '数学系', '教授', '2025-10-09 15:03:25', '2025-10-09 15:03:25');
INSERT INTO `teacher` VALUES (3, 4, 'T2021003', '外语系', '讲师', '2025-10-09 15:03:25', '2025-10-09 15:03:25');
INSERT INTO `teacher` VALUES (5, 15, 'T202291001', '算法部', '教授', '2025-11-01 22:37:33', '2025-11-01 22:37:33');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色(admin/teacher/student)',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_user_role`(`role` ASC) USING BTREE,
  INDEX `idx_user_status`(`status` ASC) USING BTREE,
  INDEX `idx_user_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '$2a$10$Hs1CNnK4jNxLu7cZhqtMDeAsl5dW3P7dcyuZpIHxqjY6RyDU9Vg0W', '系统管理员', NULL, NULL, 'ADMIN', 1, '2025-10-09 14:36:37', '2025-11-01 19:27:01');
INSERT INTO `user` VALUES (2, 't001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '张思明', 'zhang@school.com', '13800001001', 'teacher', 1, '2025-10-09 15:03:25', '2025-10-09 15:03:25');
INSERT INTO `user` VALUES (3, 't002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '李国华', 'li@school.com', '13800001002', 'teacher', 1, '2025-10-09 15:03:25', '2025-10-09 15:03:25');
INSERT INTO `user` VALUES (4, 't003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '王海燕', 'wang@school.com', '13800001003', 'teacher', 1, '2025-10-09 15:03:25', '2025-10-09 15:03:25');
INSERT INTO `user` VALUES (5, 's001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '赵一凡', 'zhao@stu.com', '13900002001', 'student', 0, '2025-10-09 15:03:25', '2025-10-09 15:03:25');
INSERT INTO `user` VALUES (6, 's002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '钱雨欣', 'qian@stu.com', '13900002002', 'student', 1, '2025-10-09 15:03:25', '2025-10-09 15:03:25');
INSERT INTO `user` VALUES (7, 's003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '孙浩然', 'sun@stu.com', '13900002003', 'student', 1, '2025-10-09 15:03:25', '2025-10-09 15:03:25');
INSERT INTO `user` VALUES (8, 's004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '周芷若', 'zhou@stu.com', '13900002004', 'student', 1, '2025-11-01 19:42:22', '2025-11-01 19:42:22');
INSERT INTO `user` VALUES (9, 's005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '吴彦祖', 'wu@stu.com', '13900002005', 'student', 1, '2025-11-01 19:42:22', '2025-11-01 19:42:22');
INSERT INTO `user` VALUES (10, 's006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '郑恺', 'zheng@stu.com', '13900002006', 'student', 1, '2025-11-01 19:42:22', '2025-11-01 19:42:22');
INSERT INTO `user` VALUES (11, 's007', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '冯程程', 'feng@stu.com', '13900002007', 'student', 1, '2025-11-01 19:42:22', '2025-11-01 19:42:22');
INSERT INTO `user` VALUES (12, 's008', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '宋茜', 'song@stu.com', '13900002008', 'student', 1, '2025-11-01 19:42:22', '2025-11-01 19:42:22');
INSERT INTO `user` VALUES (13, 's009', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '朱一龙', 'zhu@stu.com', '13900002009', 'student', 1, '2025-11-01 19:42:22', '2025-11-01 19:42:22');
INSERT INTO `user` VALUES (14, 's010', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '杨子', 'yang@stu.com', '13900002010', 'student', 1, '2025-11-01 19:42:22', '2025-11-01 19:42:22');
INSERT INTO `user` VALUES (15, 'Eighteen', '$2a$10$P.QX5MfO6tRBLZJmiE.aPeWRUl8Sw6ldkPOR3PxUsw/qEs2EW3h9y', '封灵天', '3273495516@qq.com', '13272796154', 'TEACHER', 1, '2025-11-01 22:37:33', '2025-11-01 22:37:33');
INSERT INTO `user` VALUES (16, 'King', '$2a$10$.k.JNROU52Pdt5KrpSbEsesm0QAU3vJ.1ZsE/Xm00r6NgC9luSQRO', '王新强', '3232656598982@qq.com', '13225256363', 'STUDENT', 1, '2025-11-01 22:42:46', '2025-11-01 22:42:46');

SET FOREIGN_KEY_CHECKS = 1;
