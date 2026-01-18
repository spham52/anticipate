/* eslint-disable */
importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-messaging-compat.js');

// must initialise service worker synchronously, not fetch. otherwise notifications won't work
firebase.initializeApp({
    apiKey: "AIzaSyDv9cuev1eXLtZu8EPCbhZTIIFQY3pZop0",
    authDomain: "anticipateproject.firebaseapp.com",
    projectId: "anticipateproject",
    storageBucket: "anticipateproject.firebasestorage.app",
    messagingSenderId: "497065267754",
    appId: "1:497065267754:web:c9053e96b848701958a953",
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
    self.registration.showNotification(payload.notification.title, {
        body: payload.notification.body,
        icon: '/favicon.ico'
    });
});