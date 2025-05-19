package com.example.acme_explorer.data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseService {

    private static FirebaseDatabaseService instance;
    private static FirebaseDatabase database;

    private FirebaseDatabaseService() {
        database = FirebaseDatabase.getInstance();
    }

    public static FirebaseDatabaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseDatabaseService();
        }
        return instance;
    }

    public DatabaseReference getAllTrips() {
        return database.getReference("trips");
    }
}