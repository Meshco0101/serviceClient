package com.carservice.client.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtils {

    public static String getUid() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return null;
    }

    public static FirebaseFirestore getFirestore() {
        return FirebaseFirestore.getInstance();
    }
    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }
}
