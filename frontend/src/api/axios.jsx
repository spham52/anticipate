// axios configuration file
import axios from 'axios'
import {useAuth} from "../firebase/AuthProvider";

const api = axios.create({
    baseURL: process.env.REACT_APP_API_URL
});

export const setAuthToken = (token) => {
    if (token) {
        api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    } else {
        delete api.defaults.headers.common['Authorization'];
    }
}

export default api;