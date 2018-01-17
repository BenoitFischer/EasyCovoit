package fr.toulon.seatech.easycovoit;


import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Benoit Fischer on 16/01/2018.
 */

public class InfoPerso extends AppCompatActivity {

    String strNom, strPrenom, strLieuHabitation, strLieuBoulot;
    EditText editTextNom, editTextPrenom;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.info_perso);

        // Recuperer les informations personnelles
        editTextNom = (EditText) findViewById (R.id.nomText);
        editTextPrenom = (EditText) findViewById (R.id.prenomText);



        Button valider = (Button) findViewById(R.id.valider);
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // A creer
                // Sauvegarder les info et les envoyer

                //Pour voir la valeur que j'ai récupérée
                strNom = editTextNom.getText().toString();
                strPrenom = editTextPrenom.getText().toString();
                Log.v("Nom",strNom);
                Log.v("Prenom",strPrenom);
            }
        });
    }

    public String getNom() {
        return strNom;
    }

    public void setNom(String nom) {
        this.strNom = nom;
    }

    public String getPrenom() {
        return strPrenom;
    }

    public void setPrenom(String prenom) {
        this.strPrenom = prenom;
    }

    public String getLieuHabitation() { return strLieuHabitation;   }

    public void setLieuHabitation(String lieuHabitation) {  this.strLieuHabitation = lieuHabitation;    }

    public String getLieuBoulot() {
        return strLieuBoulot;
    }

    public void setLieuBoulot(String lieuBoulot) {
        this.strLieuBoulot = lieuBoulot;
    }



}
