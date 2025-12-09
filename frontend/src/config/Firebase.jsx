import {initializeApp} from "firebase/app";
import {getMessaging, getToken, onMessage} from "firebase/messaging";

export const firebaseConfig = {
    apiKey: process.env.FIREBASE_API_KEY,
    authDomain: process.env.FIREBASE_AUTH_DOMAIN,
    projectId: process.env.FIREBASE_PROJECT_ID,
    storageBucket: process.env.FIREBASE_STORAGE_BUCKET,
    messagingSenderId: process.env.FIREBASE_MESSAGING_SENDER_ID,
    appId: process.env.FIREBASE_APP_ID,
}

export const FIREBASE_VAPID_KEY = process.env.FIREBASE_FIREBASE_VAPID_KEY;
const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

export const requestForToken = () => {
    return getToken(messaging, {vapidKey: FIREBASE_VAPID_KEY})
        .then((currentToken) => {
            if (currentToken) {
                return currentToken;
            } else {
                alert("Something went wrong with Token Registration. You will not receive any notifications.");
                return null;
            }
        })
        .catch((error) => {
            alert("Something went wrong while retrieving token. " + error);
            return null;
        })
}

onMessage(messaging, ({notification}) => {
    new Notification(notification.title, {
        body: notification.body,
        icon: notification.icon,
    });
});