package fr.toulon.seatech.easycovoit;


import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;

/**
 * Created by Benoit Fischer on 16/01/2018.
 */

// ---------------------------- Identifiants Firebase --------------
//  id : easycovoit18@gmail.com
//  mdp : easycov18

public class InfoPerso extends AppCompatActivity {

    private static final String TAG = "";
    String strMrMme="", strNom="", strPrenom="", strLieuHabitation="", strLieuBoulot ="";
    EditText editTextNom, editTextPrenom, editTextHabitat, editTextBoulot;
    Spinner spinnerMrMme;
    DatabaseReference mRootRef, mUserRef, mChildGenre, mChildNom, mChildPrenom, mChildHabitation, mChildBoulot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.info_perso);

        // Read datas from the database


        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserRef = mRootRef.child("User");
        mChildGenre = mUserRef.child("Genre");
        mChildNom = mUserRef.child("Nom");
        mChildPrenom = mUserRef.child("Prenom");
        mChildHabitation = mUserRef.child("Lieu de domicile");
        mChildBoulot = mUserRef.child("Lieu de travail");

        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //strNom = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + strNom);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");

            }
        });

        //Récuperer le nom et prénom
        editTextNom = (EditText) findViewById (R.id.nomText);
        editTextPrenom = (EditText) findViewById (R.id.prenomText);

        //Déroulement du menu déroulant
        spinnerMrMme = (Spinner) findViewById(R.id.menuDeroulantMrMme);
        String[] strChoixSpinner={"Monsieur","Madame", "Demoiseau", "Demoiselle"};
        ArrayAdapter<String> dataAdapterMrMme = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,strChoixSpinner);
        dataAdapterMrMme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMrMme.setAdapter(dataAdapterMrMme);

        //Stockage du genre Mr / Mme
        spinnerMrMme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                strMrMme = String.valueOf(spinnerMrMme.getSelectedItem()); }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        // récupération des lieux de domicile et de travail
        editTextHabitat= (EditText) findViewById (R.id.lieuHabitatText);
        editTextBoulot= (EditText) findViewById (R.id.lieuBoulotText);

        // validation et stockage des informations
        Button valider = (Button) findViewById(R.id.valider);
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //-- Stockage des informations

                //Stockage du nom et du prénom
                strNom = editTextNom.getText().toString();
                strPrenom = editTextPrenom.getText().toString();

                //Stockage du lieu de domicile et du lieu de travail
                strLieuHabitation = editTextHabitat.getText().toString();
                strLieuBoulot = editTextBoulot.getText().toString();

                // Ecriture dans la base de donnée
                mChildGenre.setValue(strMrMme);
                mChildNom.setValue(strNom);
                mChildPrenom.setValue(strPrenom);
                mChildHabitation.setValue(strLieuHabitation);
                mChildBoulot.setValue(strLieuBoulot);

                //Pour voir les valeurs que j'ai récupérées
                Log.v("Genre",strMrMme);
                Log.v("Nom",strNom);
                Log.v("Prenom",strPrenom);
                Log.v("Habitat",strLieuHabitation);
                Log.v("Boulot",strLieuBoulot);


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
