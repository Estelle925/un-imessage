import request from '../utils/request';
import type { PageResult } from './types';

export interface Recipient {
  id: number;
  name: string;
  mobile: string;
  email: string;
  openId: string;
  userId: string;
  status: number;
  createdAt: string;
  updatedAt: string;
}

export const getRecipientPage = (params: any) => {
  return request.get('/recipient/page', { params }) as Promise<PageResult<Recipient>>;
};

export const getRecipientList = () => {
  return request.get('/recipient/list');
};

export const createRecipient = (data: any) => {
  return request.post('/recipient', data);
};

export const updateRecipient = (id: number, data: any) => {
  return request.put(`/recipient/${id}`, data);
};

export const deleteRecipient = (id: number) => {
  return request.delete(`/recipient/${id}`);
};
