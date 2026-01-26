import api from '../axios.jsx'
import useAuth from '../../firebase/AuthProvider'

export const registerUserWithSensor = async (sensorID) => {
    const response = await api.post("/sensor/register", {sensorID});
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

export const findNotificationHistoryFromSensorPageable = async (sensorID, page, size) => {
    const response = await api.get("/sensor/" + sensorID + "/history", {
        params: {
            page: page,
            size: size
        }
    });
    return response.data;
}

export const findNotificationHistoryFromSensorByDate = async (sensorID, date, timezone) => {
    const response = await api.get("/sensor/" + sensorID + "/history/date", {
        params: {
            date: date,
            timezone: timezone
        }
    })
    return response.data;
}