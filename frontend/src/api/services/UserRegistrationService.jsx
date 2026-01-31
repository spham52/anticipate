import api from '../axios.jsx'

export const registerUser = async (data) => {
    return await api.post('/user/register', data);
};