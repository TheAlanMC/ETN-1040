importScripts('https://www.gstatic.com/firebasejs/10.12.1/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.12.1/firebase-messaging-compat.js');

firebase.initializeApp({
    apiKey: "AIzaSyCwJ4OGwGWFwXUS_-TxOnz_EMj0aaWhZno",
    authDomain: "etn-1040.firebaseapp.com",
    projectId: "etn-1040",
    storageBucket: "etn-1040.appspot.com",
    messagingSenderId: "770893644204",
    appId: "1:770893644204:web:8432d35a74c1c661453a09",
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage(function(payload) {
    const notificationTitle = payload.data.title;
    const notificationOptions = {
        body: payload.data.body,
        icon: payload.data.image,
    };
    self.registration.showNotification(notificationTitle, notificationOptions);
});
