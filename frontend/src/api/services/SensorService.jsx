import api from '../axios.jsx'
import useAuth from '../../firebase/AuthProvider'

export const registerUserWithSensor = async (sensorID) => {
    const response = await api.post("/sensor/register", { sensorID });
    return response.data;
};

export const findDeviceFromUser = async () => {
    const response = await api.get("/sensor");
    return response.data;
}

export const findNotificationHistoryFromSensor = async (sensorID) => {
    const response = await api.get("/sensor/" + sensorID + "/history");
    return response.data;
}