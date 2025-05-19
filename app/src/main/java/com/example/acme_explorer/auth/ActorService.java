package com.example.acme_explorer.auth;

import com.example.acme_explorer.data.Actor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;

public class ActorService {

    private static final String COLLECTION = "actors";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static Task<Void> createOrUpdateActor(Actor actor) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return db.collection(COLLECTION).document(uid).set(actor);
    }

    public static DocumentReference getCurrentActorRef() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return db.collection(COLLECTION).document(uid);
    }
}
