import request from '../utils/request';
import type { PageResult } from './types';

export interface App {
  id: number;
  appName: string;
  appKey: string;
  appSecret: string;
  description?: string;
  status: number;
  createTime: string;
}

export const getAppPage = (params: any) => {
  return request.get('/app/page', { params }) as Promise<PageResult<App>>;
};

export const getAppList = () => {
  return request.get('/app/list');
};

export const createApp = (data: any) => {
  return request.post('/app', data);
};

export const updateApp = (id: number, data: any) => {
  return request.put(`/app/${id}`, data);
};

export const deleteApp = (id: number) => {
  return request.delete(`/app/${id}`);
};

export const updateAppStatus = (id: number, status: number) => {
  return request.put(`/app/${id}/status`, null, { params: { status } });
};

export const resetAppSecret = (id: number) => {
  return request.post(`/app/${id}/reset-secret`);
};
