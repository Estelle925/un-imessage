import request from '../utils/request';
import type { LoginResult, User } from './types';

export const login = (data: any) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data,
  }) as Promise<LoginResult>;
};

export const logout = () => {
    return request({
        url: '/auth/logout',
        method: 'post'
    })
}

export const getUserInfo = () => {
    return request({
        url: '/auth/info',
        method: 'get'
    }) as Promise<User>;
}
