package de.androidcrypto.firestorechatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.androidcrypto.firestorechatapp.datafiles.Message;
import de.androidcrypto.firestorechatapp.datafiles.MessageAdapter;

//public class MainActivity extends AppCompatActivity implements View.OnClickListener {
public class MainActivityNotWorkingFbUiAuth extends AppCompatActivity {
    FirebaseAuth auth;

    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());

    FirebaseUser user;
    FirebaseFirestore database;
    Query query;
    private FirestoreRecyclerAdapter<Message, MessageAdapter.MessageHolder> adapter;
    com.google.android.material.textfield.TextInputEditText input;
    //private MultiAutoCompleteTextView input;
    com.google.android.material.textfield.TextInputLayout inputFieldLayout;
    private ProgressBar pgBar;
    RecyclerView recyclerView;
    private String userId;
    private String userName;
    Uri filePath;
    ImageView imgProfilePic;

    private LinearLayoutManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FloatingActionButton btnSend = findViewById(R.id.btnSend);
        //btnSend.setOnClickListener(this);
        input = findViewById(R.id.input);
        inputFieldLayout = findViewById(R.id.inputFieldLayout);
        pgBar = findViewById(R.id.loader);

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);

        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setLayoutManager(mManager);

        // todo enable persistence on Firestore

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Log.i("MainAcivity", " this os onCreate: " + user);

        //Check if user has signed in before else redirect to login page
        if (user == null){

            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        Toast.makeText(MainActivityNotWorkingFbUiAuth.this, "User Signed In", Toast.LENGTH_SHORT).show();
                        //signedInUser.setText(user.getEmail() + "\nDisplayName: " + user.getDisplayName());
                        //activeButtonsWhileUserIsSignedIn(true);
                        runSetupAfterSignIn();
                    } else {

                    }
                }
            };

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(true)
                            .setAvailableProviders(providers)
                            .setTheme(R.style.Theme_FirestoreChatApp)
                            .build(),
                    RC_SIGN_IN
            );

            /*
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;

             */
        }

        if (user != null) {

            userId = user.getUid();
            userName = user.getDisplayName();
            database = FirebaseFirestore.getInstance();
            query = database.collection("messages").orderBy("messageTime");
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        pgBar.setVisibility(View.GONE);

                        // Scroll to bottom on new messages
                        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                            @Override
                            public void onItemRangeInserted(int positionStart, int itemCount) {
                                mManager.smoothScrollToPosition(recyclerView, null, adapter.getItemCount());
                            }
                        });
                    }
                }
            });
            adapter = new MessageAdapter(query, userId, MainActivityNotWorkingFbUiAuth.this);
            recyclerView.setAdapter(adapter);

            inputFieldLayout.setEndIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = input.getText().toString();
                    if (TextUtils.isEmpty(message)) {
                        Toast.makeText(MainActivityNotWorkingFbUiAuth.this, "Post is post", Toast.LENGTH_LONG).show();
                        return;
                    }
                    database.collection("messages").add(new Message(userName, message, userId, 0, null));
                    input.setText("");
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void runSetupAfterSignIn() {
        userId = user.getUid();
        userName = user.getDisplayName();
        database = FirebaseFirestore.getInstance();
        query = database.collection("messages").orderBy("messageTime");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    pgBar.setVisibility(View.GONE);

                    // Scroll to bottom on new messages
                    adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                        @Override
                        public void onItemRangeInserted(int positionStart, int itemCount) {
                            mManager.smoothScrollToPosition(recyclerView, null, adapter.getItemCount());
                        }
                    });
                }
            }
        });
        adapter = new MessageAdapter(query, userId, MainActivityNotWorkingFbUiAuth.this);
        recyclerView.setAdapter(adapter);

        inputFieldLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = input.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(MainActivityNotWorkingFbUiAuth.this, "Post is post", Toast.LENGTH_LONG).show();
                    return;
                }
                database.collection("messages").add(new Message(userName, message, userId, 0, null));
                input.setText("");
                adapter.notifyDataSetChanged();
            }
        });

    }

    /* used for FloatingButton
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnSend){
            String message = input.getText().toString();
            if(TextUtils.isEmpty(message)){
                Toast.makeText(MainActivity.this, "Post is post", Toast.LENGTH_LONG).show();
                return;
            }
            database.collection("messages").add(new Message(userName, message, userId, 0, null));
            input.setText("");
        }
    }
     */

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_contact:
                grabImage();
                break;
            case R.id.menu_sign_out:
                auth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
        return true;
    }

    public void grabImage(){
        /*
        Build a dialogView for user to set profile image
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityNotWorkingFbUiAuth.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.profile_image, null);
        builder.setView(dialogView);

        final AlertDialog dialog= builder.create();
        dialog.setCancelable(false);
        dialog.show();

        Button btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        imgProfilePic = dialog.findViewById(R.id.imgProfilePic);
        imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Use android-image-cropper library to grab and crop image
                CropImage.activity()
                        .setFixAspectRatio(true)
                        .start(MainActivityNotWorkingFbUiAuth.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                uploadImage();
                imgProfilePic.setImageURI(filePath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("profile_images").child(userId);
        ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String url = uri.toString();
                                        Map<String, String> imageLocation = new HashMap<>();
                                        imageLocation.put("a6_imageUrl", url);
                                        database.collection("users").document(userId).set(imageLocation);
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT).show();
                                        imgProfilePic.setImageURI(filePath);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage((int) progress + "%" + " completed" );
                    }
                });
    }
}
