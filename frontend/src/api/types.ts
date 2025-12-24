export interface User {
  id: number;
  username: string;
  nickname?: string;
  email?: string;
  phone?: string;
  status: number;
  createTime: string;
}

export interface LoginResult {
  token: string;
  user: User;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}
