package com.dramanevalentin.applicationjava;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    ImageView ImgUserPhoto;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;

    // Création d'objets de type EditText, ProgressBar, Button et FireBase
    private EditText userEmail,userPassword,userPAssword2,userName;
    private ProgressBar loadingProgress;
    private Button btn;
    private FirebaseAuth fireAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ici nous allons initialiser les Views de
        userEmail = findViewById(R.id.mail); // On cible l'id du champs Email
        userPassword = findViewById(R.id.password); // On cible l'id du champs password
        userPAssword2 = findViewById(R.id.password2); // On cible l'id du champs confirm password
        userName = findViewById(R.id.name); // On cible l'id du champs name
        loadingProgress = findViewById(R.id.progressBar); // On cible l'id de la progressBar
        btn = findViewById(R.id.btn); // On cible l'id du bouton REGISTER
        loadingProgress.setVisibility(View.INVISIBLE); // On set une methode INVISIBLE à notre élément loadingProgress
        fireAuth = FirebaseAuth.getInstance(); // On instancie un element d'authentification FireBase


        /*
        *   Lorsque l'utilisateur va cliquer sur le bouton REGISTER, une nouvelle vue va apparaître.
        *   Mais avant qu'elle aparaisse, nous faisont apparître notre progressBar avec la methode,
        *   `VISIBLE`.
        * */
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);

                // On utilise Final pour rendre les valeurs figé, donc pas modifiable pour la suite du programme
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String password2 = userPAssword2.getText().toString();
                final String name = userName.getText().toString();

                /*

                On procède à une vérification si nos variables ne sont pas vide, auquel cas, on
                rend la progresseBar INVISIBLE et la vue reste VISIBLE, avec un petit message d'erreur,
                pour l'utilisateur.

                 */
                if( email.isEmpty() || name.isEmpty() || password.isEmpty()  || !password.equals(password2)) {
                    btn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                    displayMessage("Verifier les informations saisies..."); ;
                } else {
                    // La methode CreateUserAccount créer un compte si les infos sont valides
                    CreateUserAccount(email,name,password);
                }
            }
        });

        ImgUserPhoto = findViewById(R.id.userProfilePhoto) ;

        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 22) {

                    checkAndRequestForPermission();


                } else {
                    openGallery();
                }
            }
        });

    }

    // Methode permettant la création d'un compte
    private void CreateUserAccount(final String email, final String name, final String password) {

        // methode de fierBase permettant la création d'un compte avec l'email / password
        fireAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Si tout est ok, on affiche un msg à l'utilisateur
                            displayMessage("Votre compte à bien été créé ");
                            // On propose ensuite à user de modifier son nom et sa photo
                            updateUserInfo( name ,pickedImgUri,fireAuth.getCurrentUser());
                        } else {
                            // Dans le cas échéan, on indique à l'usilisateur que la création à échoué
                            displayMessage("La création du compte a échoué : " + task.getException().getMessage());
                            btn.setVisibility(View.VISIBLE); // Le bouton repasse à l'état VISIBLE
                            loadingProgress.setVisibility(View.INVISIBLE); // La progresseBar INVISIBLE
                        }
                    }
                });
    }


    // Mettre à jour le nom de l'utilisateur
    // update user photo and name
    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {

        // first we need to upload user photo to firebase storage and get url

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // L'image est chargé
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        // uri contient l'url de l'image

                        // Ici on fait appel à une class FireBase elle permet, comme son nom l'indique de changer le profile utilisateur
                        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name) // C'est le nom que l'on souhaite changer
                                .setPhotoUri(uri)
                                .build();

                        // puis on met à jour l'argument de type FirebaseUser
                        currentUser.updateProfile(profleUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // Si tout est 'ok' on affiche un msg à l'utilisateur
                                        if (task.isSuccessful()) {
                                            // user info updated successfully
                                            displayMessage("Register Complete");
                                            updateUI();
                                        }

                                    }
                                });
                    }
                });
            }
        });
    }

    // Methode permettant la mise à jour et l'affichage de la page d'accueil
    private void updateUI() {
        Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(homeActivity);
        finish();
    }

    // Methode pour le renvoie vers la page LoginActivity
    private void loginPage() {
        Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }



    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }



    private void checkAndRequestForPermission() {


        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(RegisterActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        }
        else
            openGallery();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {
            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            ImgUserPhoto.setImageURI(pickedImgUri);
        }
    }

}


/* -------------------- Ressources / Remerciements -------------------- */
/*
    Ressources :
        - Doc Android.Developper : https://developer.android.com/guide/topics/resources/providing-resources
        - Doc Google.Developper : https://developers.google.com/admob/android/quick-start#update_your_androidmanifestxml
        - Youtube tutoriels : https://www.youtube.com/
        - Doc FireBase :
            - https://firebase.google.com/
            - https://firebase.google.com/docs/auth/web/manage-users
        - StackOverFlow :
            - https://stackoverflow.com/questions/30568641/the-import-android-support-v7-app-actionbardrawertoggle-cannot-be-resolved
            - https://stackoverflow.com/questions/25610727/adding-external-library-in-android-studio
            - https://stackoverflow.com/questions/34842610/cannot-resolve-symbol-toolbar-r-id-toolbar
            - https://stackoverflow.com/questions/46900261/error-ambiguous-method-call-both-findviewbyid-int-in-appcompactactivity-and
            - https://stackoverflow.com/questions/49280632/error9-5-error-resource-androidattr-dialogcornerradius-not-found
            - https://stackoverflow.com/questions/49891730/invoke-customs-are-only-supported-starting-with-android-0-min-api-26
            - https://stackoverflow.com/questions/51341627/android-gives-error-cannot-fit-requested-classes-in-a-single-dex-file
            - https://stackoverflow.com/questions/52786963/unable-to-get-provider-com-google-android-gms-ads-mobileadsinitprovider-java-la
            - https://stackoverflow.com/questions/20081217/java-lang-illegalstateexception-scrollview-can-host-only-one-direct-child/20081261
        - Autres: https://www.11zon.com/android/android_navigation_drawer.php


                   *******************  REMERCIEMENT  *******************

			Nous souhaitons aussi remercier le professeur pour nous avoir apporter
			ça connaissance  et ça science !  Nous sommes reconnaissants pour ça
			car le savoir est quelque chose de très précieux et ce qui nous l’enseigne
			mérite d’être remercié !!!

 */



/* -------------------- By Valentin And Dramane E3B  -------------------- */