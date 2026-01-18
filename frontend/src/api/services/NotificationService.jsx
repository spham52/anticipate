import api from '../axios';
import { requestForToken } from "../../firebase/Firebase";

export const saveFCMToken = async () => {
    const token = await requestForToken();

    if (token) {
        await api.post("/token/register", {tokenID: token, platform: "desktop", appVersion: "1.0.0"});
    }
}