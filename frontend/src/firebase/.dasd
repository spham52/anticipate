import {initializeApp} from "firebase/app";
import {getMessaging, getToken, onMessage} from "firebase/messaging";
import { getAuth } from "firebase/auth";

export const firebaseConfig = {
    apiKey: process.env.REACT_APP_FIREBASE_API_KEY,
    authDomain: process.env.REACT_APP_FIREBASE_AUTH_DOMAIN,
    projectId: process.env.REACT_APP_FIREBASE_PROJECT_ID,
    storageBucket: process.env.REACT_APP_FIREBASE_STORAGE_BUCKET,
    messagingSenderId: process.env.REACT_APP_FIREBASE_MESSAGING_SENDER_ID,
    appId: process.env.REACT_APP_FIREBASE_APP_ID,
}

export const FIREBASE_VAPID_KEY = process.env.FIREBASE_FIREBASE_VAPID_KEY;
const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
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

export const onLogout = (e) => {
    auth.signOut();
}