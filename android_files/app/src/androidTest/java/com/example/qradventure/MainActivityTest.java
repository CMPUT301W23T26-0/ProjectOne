package com.example.qradventure;


import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import android.util.Log;

import com.example.qradventure.qrcode.QRCode;
import com.example.qradventure.users.UserDataClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for MainActivity. All the UI tests are written here. Robotium test framework is used
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest{

    private Solo solo;
    private FirebaseFirestore db;
    private UserDataClass user;
    private String TAG;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);
    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
        db = FirebaseFirestore.getInstance();
        user = UserDataClass.getInstance();
        TAG = "TEST: ";
    }

    /**
     * Tests if login screen appears properly
     * @throws Exception
     */
    @Test
    public void testLogIn() {
        String id = user.getUserPhoneID();
        final Boolean[] loggedIn = {false};

        DocumentReference docRef = db.collection("Users").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Log.d(TAG, "--------------- Doc Exists -----------------");
                        loggedIn[0] = true;
                    }
                }
            }
        });

        if (loggedIn[0]) {
            solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        } else {
            solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        }
    }

    @Test
    public void addQR() {
        String id = user.getUserPhoneID();
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        QRCode code = new QRCode("028cdf2261a35d620d7a4ecd9466cb3ba5d39dee006a86e7483a8dcbdf695695");
        CollectionReference cRef = db.collection("Users").document(id).collection("Codes");
        Map<String, Object> newCode = new HashMap<>();
        newCode.put("name", code.getName());
        newCode.put("score", code.getScore());
        newCode.put("hash", code.getHashValue());
        cRef.add(newCode);
    }

    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}