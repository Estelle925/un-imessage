import request from '../utils/request';
import type { PageResult } from './types';

export interface Channel {
  id: number;
  name: string;
  type: string;
  provider: string;
  configJson: string;
  status: number;
  createTime: string;
}

export const getChannelPage = (params: any) => {
  return request.get('/channel/page', { params }) as Promise<PageResult<Channel>>;
};

export const getChannelList = (type?: string) => {
  return request.get('/channel/list', { params: { type } });
};

export const createChannel = (data: any) => {
  return request.post('/channel', data);
};

export const updateChannel = (id: number, data: any) => {
  return request.put(`/channel/${id}`, data);
};

export const deleteChannel = (id: number) => {
  return request.delete(`/channel/${id}`);
};

export const updateChannelStatus = (id: number, status: number) => {
  return request.put(`/channel/${id}/status`, null, { params: { status } });
};

export const testChannel = (id: number) => {
  return request.post(`/channel/${id}/test`);
};
