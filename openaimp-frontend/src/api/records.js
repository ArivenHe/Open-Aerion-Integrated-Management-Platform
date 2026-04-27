import api from '@/api/http'

export const fetchFlightRecords = async (userCid) => api.get(`/flight-records/users/${userCid}`)

export const createFlightRecord = async (userCid, payload) =>
  api.post(`/flight-records/users/${userCid}`, payload)

export const updateFlightRecord = async (id, payload) => api.put(`/flight-records/${id}`, payload)

export const deleteFlightRecord = async (id) => api.delete(`/flight-records/${id}`)

export const fetchControlRecords = async (userCid) => api.get(`/control-records/users/${userCid}`)

export const createControlRecord = async (userCid, payload) =>
  api.post(`/control-records/users/${userCid}`, payload)

export const updateControlRecord = async (id, payload) =>
  api.put(`/control-records/${id}`, payload)

export const deleteControlRecord = async (id) => api.delete(`/control-records/${id}`)
