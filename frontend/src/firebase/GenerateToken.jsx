import {requestForToken} from "./Firebase"

export default async function GenerateToken() {
    const permission = await Notification.requestPermission();
    if (permission === "granted") {
        const token = await requestForToken();
        return token;
    }
    return null;
}