package com.dramanevalentin.applicationjava;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    // Création d'objets
    private EditText userMail,userPassword;
    private Button btnLogin;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;
    private Intent HomeActivity;
    private ImageView loginPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // On cible les éléments du layout loginActivity
        userMail = findViewById(R.id.login_mail);
        userPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.loginBtn);
        loginProgress = findViewById(R.id.login_progress);
        mAuth = FirebaseAuth.getInstance();
        HomeActivity = new Intent(this,com.dramanevalentin.applicationjava.HomeActivity.class);
        loginPhoto = findViewById(R.id.login_photo);
        loginPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Création d'une intention, lorsque l'on clique sur le logo user
                Intent registerActivity = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });

        // On rend la progress bar est invisible
        loginProgress.setVisibility(View.INVISIBLE);
        // Lorsque l'on clique sur le btnLogin,
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // On rend visible la progress bar et fait disparaître le btnLogin
                loginProgress.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                // On fige les valeur mail et password pour les vérifier ..
                final String mail = userMail.getText().toString();
                final String password = userPassword.getText().toString();

                // Si l'un ou l'autre est vide :
                if (mail.isEmpty() || password.isEmpty()) {
                    displayMessage("Vérifer votre saisie ...");
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                } else {
                    // Sinon, l'utilisateur est connécté ***
                    signIn(mail,password);
                }
            }
        });


    }

    // Methode de connexion, requiere l'email et le passeword qui sont ensuite utilisé par une condition
    private void signIn(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    loginProgress.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    updateUI();

                } else {
                    displayMessage(task.getException().getMessage());
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    // Methode de redirection vers la HomePage
    private void updateUI() {
        startActivity(HomeActivity);
        finish();

    }

    // Methode permettant l'affichage de message
    private void displayMessage(String text) {

        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            // Si l'utilisateur est connecté, alors il a accès et est redirigé vers la HomePage
            updateUI();
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
