package com.dramanevalentin.applicationjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {
    /* Création de l'objet textViewResult de type TextView pour l'affichage à l'écran  */
    private TextView textViewResult;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* on assigne à notre objet le lien entre notre id, de la balise `activity_main.xml`
         *   pour créer le lien entre eux
         *  */
        textViewResult = findViewById(R.id.text_view_result);

        /* Instanciation d'un objet 'retrofit'
         *   on appel la methode builder, methode qui appel une factory adapter pour
         *   prendre en charge le type de retour. Pour finir, on fait appel à la méthode
         *   addConverterFactory pour la serialization et déserialization d'un objet.
         *   Pour finir, on appel la methode build() qui construit une instance de Retrofit
         *   et utilise les valeurs qu'on lui a configuées
         *  */
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.vasedhonneurofficiel.com/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        /* Ici on fait appel à notre interface VasedhonneurofficielApi */
        VasedhonneurofficielApi vasedhonneurofficielApi = retrofit.create(VasedhonneurofficielApi.class);

        Call<List<Products>> call = vasedhonneurofficielApi.getProductsList();


        call.enqueue(new Callback<List<Products>>() {
            @Override
            public void onResponse(Call<List<Products>> call, Response<List<Products>> response) {
                if (!response.isSuccessful()) {
                    textViewResult.setText("Code :" + response.code());
                    return;
                }

                List<Products> products = response.body();

                for (Products product : products) {
                    String content = "";
                    content += "ID" + product.getId() + "\n";
                    content += "ID" + product.getTitle() + "\n";
                    content += "ID" + product.getFilename() + "\n";

                    textViewResult.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Products>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });

        // Retour à la page d'accueil
        this.btn = findViewById(R.id.retour_accueil);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pageAccueil = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(pageAccueil);
                finish();
            }
        });
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