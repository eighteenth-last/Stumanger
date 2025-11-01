import request from './request'

/**
 * 获取课程列表
 */
export function getCourseList(params) {
  return request({
    url: '/courses',
    method: 'get',
    params
  })
}

/**
 * 获取课程详情
 */
export function getCourseDetail(id) {
  return request({
    url: `/courses/${id}`,
    method: 'get'
  })
}

/**
 * 创建课程
 */
export function createCourse(data) {
  return request({
    url: '/courses',
    method: 'post',
    data
  })
}

/**
 * 更新课程信息
 */
export function updateCourse(id, data) {
  return request({
    url: `/courses/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除课程
 */
export function deleteCourse(id) {
  return request({
    url: `/courses/${id}`,
    method: 'delete'
  })
}

/**
 * 根据教师ID获取课程列表
 */
export function getCoursesByTeacher(teacherId) {
  return request({
    url: `/courses/by-teacher/${teacherId}`,
    method: 'get'
  })
}

/**
 * 批量删除课程
 */
export function batchDeleteCourses(ids) {
  return request({
    url: '/courses/batch',
    method: 'delete',
    data: ids
  })
}
