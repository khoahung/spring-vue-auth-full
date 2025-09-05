
import axios from 'axios';
import { jwtDecode } from 'jwt-decode'

const API_BASE = import.meta.env.VITE_API_BASE || 'http://192.168.1.101:8082';
const AUTH_BASE = import.meta.env.VITE_AUTH_BASE || 'http://192.168.1.101:8081';

let isRefreshing = false;
let refreshQueue = [];

function setAuthHeader(config){
  const token = localStorage.getItem('accessToken');
  if (token) config.headers = { ...(config.headers||{}), Authorization: `Bearer ${token}` };
  return config;
}

const api = axios.create({ baseURL: API_BASE });
api.interceptors.request.use(setAuthHeader);

api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config;
    if (error.response && error.response.status === 401 && !original._retry){
      original._retry = true;
      if (!isRefreshing){
        isRefreshing = true;
        try{
          const refreshToken = localStorage.getItem('refreshToken');
          const r = await axios.post(`${AUTH_BASE}/auth/refresh`, { refreshToken });
          localStorage.setItem('accessToken', r.data.accessToken);
          localStorage.setItem('refreshToken', r.data.refreshToken);
          refreshQueue.forEach(cb => cb());
          refreshQueue = [];
          return api(original);
        }catch(e){
          refreshQueue = [];
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          window.location.href = '/';
          return Promise.reject(e);
        }finally{ isRefreshing = false; }
      } else {
        return new Promise(resolve => {
          refreshQueue.push(() => resolve(api(original)));
        });
      }
    }
    return Promise.reject(error);
  }
);

export const auth = {
  async login(username, password){
    const r = await axios.post(`${AUTH_BASE}/auth/login`, { username, password });
    localStorage.setItem('accessToken', r.data.accessToken);
    localStorage.setItem('refreshToken', r.data.refreshToken);
    localStorage.setItem('username', jwtDecode(r.data.accessToken).sub);
    localStorage.setItem('role', jwtDecode(r.data.accessToken).role);
    return r.data;
  },
  async register(username, password, role){
	const token = localStorage.getItem("accessToken")
    return await axios.post(`${API_BASE}/api/register`, {username, password, role},{
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer "+token,
        },
      });
  },
  async update(id, username, password, role){
	const token = localStorage.getItem("accessToken")
    return await axios.post(`${API_BASE}/api/updateUser`, {id,username, password, role},{
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer "+token,
        },
      });
  },
  async deleteUser(id){
	const token = localStorage.getItem("accessToken")
    return await axios.post(`${API_BASE}/api/deleteUser`,{id},{
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer "+token,
        },
      });
  },
  logout(){
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
  }
};

export default api;
