importScripts('https://www.gstatic.com/firebasejs/10.12.1/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.12.1/firebase-messaging-compat.js');

firebase.initializeApp({
    apiKey: "AIzaSyA6vP_lILs4xOGDK0FAXKcfpw3-Em75J7s",
    authDomain: "laboratorio-multimedia.firebaseapp.com",
    projectId: "laboratorio-multimedia",
    storageBucket: "laboratorio-multimedia.appspot.com",
    messagingSenderId: "299630257084",
    appId: "1:299630257084:web:3ed2040af225c2905901b9",
});

const messaging = firebase.messaging();

const channel = new BroadcastChannel('firebase-messaging');


messaging.onBackgroundMessage(function(payload) {
    const notificationTitle = payload.data.title;
    const notificationOptions = {
        body: payload.data.body,
        icon: payload.data.image,
    };
    self.registration.showNotification(notificationTitle, notificationOptions);

    channel.postMessage(payload);
});
