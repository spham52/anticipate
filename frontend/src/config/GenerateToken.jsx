import React, {useEffect, useState} from 'react'
import {requestForToken} from "../config/firebase"

export default function GenerateToken() {
    const [token, setToken] = useState();

    useEffect(() => {
        const getToken = async () => {
            const permission = await Notification.requestPermission();
            if (permission === "granted") {
                const token = await requestForToken();
                if (token) {
                    setToken(token);
                }
            }
        }
        getToken();
    }, []);
}