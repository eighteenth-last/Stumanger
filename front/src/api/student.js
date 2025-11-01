import request from './request'

/**
 * 获取学生列表
 */
export function getStudentList(params) {
  return request({
    url: '/students',
    method: 'get',
    params
  })
}

/**
 * 获取学生详情
 */
export function getStudentDetail(id) {
  return request({
    url: `/students/${id}`,
    method: 'get'
  })
}

/**
 * 创建学生
 */
export function createStudent(data) {
  return request({
    url: '/students',
    method: 'post',
    data
  })
}

/**
 * 更新学生信息
 */
export function updateStudent(id, data) {
  return request({
    url: `/students/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除学生
 */
export function deleteStudent(id) {
  return request({
    url: `/students/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除学生
 */
export function batchDeleteStudents(ids) {
  return request({
    url: '/students/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 批量分配班级
 */
export function batchAssignClass(data) {
  return request({
    url: '/students/batch-assign-class',
    method: 'post',
    data
  })
}

/**
 * 获取未分配班级的学生
 */
export function getStudentsWithoutClass() {
  return request({
    url: '/students/without-class',
    method: 'get'
  })
}

/**
 * 导出学生数据
 */
export function exportStudents(params) {
  return request({
    url: '/students/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 下载学生导入模板
 */
export function downloadStudentTemplate() {
  return request({
    url: '/students/template',
    method: 'get',
    responseType: 'blob'
  })
}
