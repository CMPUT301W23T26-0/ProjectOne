package com.example.qradventure;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.FirestoreClient;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
     * Gets the Activity
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
            solo.assertCurrentActivity("Wrong Activity" ,MainActivity.class);
        } else {
            solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        }
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