import request from './request'

/**
 * 获取成绩列表
 */
export function getScoreList(params) {
  return request({
    url: '/scores',
    method: 'get',
    params
  })
}

/**
 * 获取成绩详情
 */
export function getScoreDetail(id) {
  return request({
    url: `/scores/${id}`,
    method: 'get'
  })
}

/**
 * 创建成绩记录
 */
export function createScore(data) {
  return request({
    url: '/scores',
    method: 'post',
    data
  })
}

/**
 * 更新成绩
 */
export function updateScore(id, data) {
  return request({
    url: `/scores/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除成绩记录
 */
export function deleteScore(id) {
  return request({
    url: `/scores/${id}`,
    method: 'delete'
  })
}

/**
 * 根据学生ID获取成绩列表
 */
export function getScoresByStudent(studentId) {
  return request({
    url: `/scores/by-student/${studentId}`,
    method: 'get'
  })
}

/**
 * 根据课程ID获取成绩列表
 */
export function getScoresByCourse(courseId) {
  return request({
    url: `/scores/by-course/${courseId}`,
    method: 'get'
  })
}

/**
 * 获取课程成绩统计
 */
export function getCourseScoreStatistics(courseId, semester) {
  return request({
    url: `/scores/statistics/course/${courseId}`,
    method: 'get',
    params: { semester }
  })
}

/**
 * 获取学生成绩统计
 */
export function getStudentScoreStatistics(studentId) {
  return request({
    url: `/scores/statistics/student/${studentId}`,
    method: 'get'
  })
}

/**
 * 获取班级成绩统计
 */
export function getClassScoreStatistics(classId, semester) {
  return request({
    url: `/scores/statistics/class/${classId}`,
    method: 'get',
    params: { semester }
  })
}

/**
 * 获取所有学期列表
 */
export function getAllSemesters() {
  return request({
    url: '/scores/semesters',
    method: 'get'
  })
}

/**
 * 批量导入成绩
 */
export function batchImportScores(data) {
  return request({
    url: '/scores/batch-import',
    method: 'post',
    data
  })
}

/**
 * 导出成绩数据
 */
export function exportScores(params) {
  return request({
    url: '/scores/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 下载成绩导入模板
 */
export function downloadScoreTemplate() {
  return request({
    url: '/scores/template',
    method: 'get',
    responseType: 'blob'
  })
}

/**
 * 批量删除成绩
 */
export function batchDeleteScores(ids) {
  return request({
    url: '/scores/batch',
    method: 'delete',
    data: ids
  })
}
