import request from './request'

/**
 * 获取教师列表
 */
export function getTeacherList(params) {
  return request({
    url: '/teachers',
    method: 'get',
    params
  })
}

/**
 * 获取教师详情
 */
export function getTeacherDetail(id) {
  return request({
    url: `/teachers/${id}`,
    method: 'get'
  })
}

/**
 * 创建教师
 */
export function createTeacher(data) {
  return request({
    url: '/teachers',
    method: 'post',
    data
  })
}

/**
 * 更新教师信息
 */
export function updateTeacher(id, data) {
  return request({
    url: `/teachers/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除教师
 */
export function deleteTeacher(id) {
  return request({
    url: `/teachers/${id}`,
    method: 'delete'
  })
}

/**
 * 获取所有部门列表
 */
export function getAllDepartments() {
  return request({
    url: '/teachers/departments',
    method: 'get'
  })
}

/**
 * 获取所有职称列表
 */
export function getAllTitles() {
  return request({
    url: '/teachers/titles',
    method: 'get'
  })
}

/**
 * 批量删除教师
 */
export function batchDeleteTeachers(ids) {
  return request({
    url: '/teachers/batch',
    method: 'delete',
    data: ids
  })
}
