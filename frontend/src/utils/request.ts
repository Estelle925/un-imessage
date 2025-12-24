import axios from 'axios';
import { message } from 'antd';

const request = axios.create({
  baseURL: '/api/v1', // Match backend prefix
  timeout: 5000,
});

// Request interceptor
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('uni-message-token');
    if (token) {
      config.headers['uni-message-token'] = token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
request.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (res.code === 200) {
      return res.data;
    } else {
      message.error(res.message || 'Error');
      if (res.code === 401) {
          // Redirect to login
          localStorage.removeItem('uni-message-token');
          window.location.href = '/login';
      }
      return Promise.reject(new Error(res.message || 'Error'));
    }
  },
  (error) => {
    // Handle 500 error which might be "NotLoginException" wrapped in 500
    if (error.response) {
        const { status, data } = error.response;
        // If 401 or backend explicitly says not logged in (even in 500)
        if (status === 401 || (status === 500 && data && (data.message || '').includes('NotLoginException'))) {
            localStorage.removeItem('uni-message-token');
            localStorage.removeItem('user');
            window.location.href = '/login';
            return Promise.reject(error);
        }
    }
    
    message.error(error.message || 'Network Error');
    return Promise.reject(error);
  }
);

export default request;
