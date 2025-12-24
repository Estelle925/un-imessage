import request from '../utils/request';
import type { PageResult, User } from './types';

export type { User };

export const getUserPage = (params: any) => {
  return request.get('/user/page', { params }) as Promise<PageResult<User>>;
};

export const createUser = (data: any) => {
  return request.post('/user', data);
};

export const updateUser = (id: number, data: any) => {
  return request.put(`/user/${id}`, data);
};

export const deleteUser = (id: number) => {
  return request.delete(`/user/${id}`);
};

export const resetPassword = (id: number, data: any) => {
  return request.put(`/user/${id}/password`, data);
};
