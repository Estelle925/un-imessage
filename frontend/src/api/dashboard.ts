import request from '../utils/request';

export interface ChartData {
    name: string;
    value: number;
}

export interface DashboardStats {
    appCount: number;
    msgCount: number;
    userCount: number;
    successRate: number;
    trend: ChartData[];
    channelDist: ChartData[];
    statusDist: ChartData[];
}

export const getDashboardStats = () => {
    return request.get('/dashboard/stats') as Promise<DashboardStats>;
};
