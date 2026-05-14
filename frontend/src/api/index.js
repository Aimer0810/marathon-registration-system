import axios from 'axios'
import { clearAuth, getToken } from '../utils/auth'

const instance = axios.create({
  baseURL: '/api',
  timeout: 5000,
  withCredentials: false
})

instance.interceptors.request.use(config => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  config.headers['Content-Type'] = 'application/json; charset=UTF-8'
  return config
})

instance.interceptors.response.use(
  response => response.data,
  error => {
    if (error?.response?.status === 401) {
      clearAuth()
    }
    console.error(error)
    return Promise.reject(error)
  }
)

export default {
  login(data) { return instance.post('/login', data) },
  register(data) { return instance.post('/register', data) },
  logout() { return instance.get('/logout') },
  getCurrentUser() { return instance.get('/current-user') },

  updateProfile(data) { return instance.put('/user/profile', data) },
  changePassword(oldPassword, newPassword) {
    return instance.put('/user/password', null, { params: { oldPassword, newPassword } })
  },

  getEventList(params) { return instance.get('/events', { params }) },
  getEventDetail(id) { return instance.get(`/events/${id}`) },

  submitRegistration(data) { return instance.post('/registrations', data) },
  getMyRegistrations() { return instance.get('/user/registrations') },
  cancelRegistration(id) { return instance.delete(`/registrations/${id}`) },

  getAnnouncements() { return instance.get('/announcements', { params: { all: true } }) },
  getAnnouncementsPage(pageNum, pageSize) {
    return instance.get('/announcements', { params: { pageNum, pageSize } })
  },
  getAnnouncementDetail(id) { return instance.get(`/announcements/${id}`) },

  submitMessage(data) { return instance.post('/messages', data) },
  getMyMessages() { return instance.get('/messages/user') },

  aiChat(message) { return instance.post('/ai/chat', { message }, { timeout: 30000 }) },
  getAiHistory() { return instance.get('/ai/history', { timeout: 15000 }) },

  addFavorite(eventId) { return instance.post('/favorites', null, { params: { eventId } }) },
  removeFavorite(eventId) { return instance.delete('/favorites', { params: { eventId } }) },
  getMyFavorites() { return instance.get('/favorites/user') },
  checkFavorite(eventId) { return instance.get('/favorites/check', { params: { eventId } }) },

  adminGetEvents() { return instance.get('/admin/events') },
  adminAddEvent(data) { return instance.post('/admin/events', data) },
  adminUpdateEvent(id, data) { return instance.put(`/admin/events/${id}`, data) },
  adminDeleteEvent(id) { return instance.delete(`/admin/events/${id}`) },

  adminGetUsers(keyword) { return instance.get('/admin/users', { params: { keyword } }) },
  adminUpdateUserStatus(id, status) { return instance.put(`/admin/users/${id}/status`, null, { params: { status } }) },
  adminResetPassword(id, password) { return instance.post(`/admin/users/${id}/reset-password`, null, { params: { password } }) },

  adminGetRegistrations(params) { return instance.get('/admin/registrations', { params }) },
  adminAuditRegistration(id, status) { return instance.put(`/admin/registrations/${id}/audit`, null, { params: { status } }) },

  adminGetAnnouncements() { return instance.get('/admin/announcements') },
  adminAddAnnouncement(data) { return instance.post('/admin/announcements', data) },
  adminUpdateAnnouncement(id, data) { return instance.put(`/admin/announcements/${id}`, data) },
  adminDeleteAnnouncement(id) { return instance.delete(`/admin/announcements/${id}`) },

  adminGetMessages() { return instance.get('/admin/messages') },
  adminReplyMessage(id, reply) { return instance.put(`/admin/messages/${id}/reply`, null, { params: { reply } }) }
}
