// axios configuration file
import axios from 'axios'
import {useAuth} from "../firebase/AuthProvider";

const api = axios.create({
    baseURL: process.env.REACT_APP_API_URL
});

api.interceptors.request.use(async (config) => {
    const user = useAuth();

    if (user) {
        const token = await user.getIdToken();
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
})

export default api;