import request from './request'

/**
 * 获取班级列表
 */
export function getClassList(params) {
  return request({
    url: '/classes',
    method: 'get',
    params
  })
}

/**
 * 获取班级详情
 */
export function getClassDetail(id) {
  return request({
    url: `/classes/${id}`,
    method: 'get'
  })
}

/**
 * 创建班级
 */
export function createClass(data) {
  return request({
    url: '/classes',
    method: 'post',
    data
  })
}

/**
 * 更新班级信息
 */
export function updateClass(id, data) {
  return request({
    url: `/classes/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除班级
 */
export function deleteClass(id) {
  return request({
    url: `/classes/${id}`,
    method: 'delete'
  })
}

/**
 * 获取所有专业列表
 */
export function getAllMajors() {
  return request({
    url: '/classes/majors',
    method: 'get'
  })
}

/**
 * 获取所有年级列表
 */
export function getAllGrades() {
  return request({
    url: '/classes/grades',
    method: 'get'
  })
}

/**
 * 批量删除班级
 */
export function batchDeleteClasses(ids) {
  return request({
    url: '/classes/batch',
    method: 'delete',
    data: ids
  })
}
