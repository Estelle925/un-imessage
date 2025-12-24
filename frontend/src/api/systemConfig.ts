import request from '../utils/request';

export interface SysConfig {
    id?: number;
    systemName?: string;
    logo?: string;
    icon?: string;
}

export const getSystemConfig = () => {
    return request.get('/system/config') as Promise<SysConfig>;
};

export const updateSystemConfig = (data: SysConfig) => {
    return request.post('/system/config', data) as Promise<void>;
};
