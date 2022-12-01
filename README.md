# Firestore chat app

This is a renewed version of the original "Soccer Gist" app created by **Mendhie Emmanuel**. The original 
source is available here: https://github.com/megamendhie/Soccer-Gist 

I changed most dependency versions so that it runs on modern Android versions, it is tested up to 
Android 12 (SDK 33) and Gradle 7.4.1.

This app is using **Firebase Auth** for user authentication with email and password, **Firestore Database** 
for storing the chats and user data and **Firebase Storage** for storing the user's profile images.

You need to setup your own Firebase service via Firebase console.


Original description:

Soccer Gist is chatroom app developed to demo how to build real-time android chatroom with Firebase.  Here's the article [link](https://medium.com/@mendhie/building-real-time-android-chatroom-with-firebase-99a5b51cb4f7)

  ![chatroom](https://cdn-images-1.medium.com/max/800/1*N6dpJZfV_2EymLTQsEREFw.png)

The article covers:
 - Creating Firebase project
 - Setting up Firebase authentication, cloud firestore, and storage
 - User authentication with email/password
 - Using FirebaseUI for Cloud Firestore
 - CRUD operation on Cloud Firestore

Cheers!
