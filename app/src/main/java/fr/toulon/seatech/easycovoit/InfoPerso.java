package fr.toulon.seatech.easycovoit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


// ---------------------------- Identifiants Firebase --------------
//  id : easycovoit18@gmail.com
//  mdp : easycov18

public class InfoPerso extends AppCompatActivity {

    private static final String TAG = "";
    String uid, strMrMme="", strNom="", strPrenom="", strLieuHabitation="", strLieuBoulot ="";
    String genreDatabasase, nomDatabase, prenomDatabase, lieuHabitatDatabase, lieuBoulotDatabase;
    EditText editTextNom, editTextPrenom, editTextHabitat, editTextBoulot;
    Spinner spinnerMrMme;
    DatabaseReference mRootRef, mIDRef, mInfoPersoRef, mUserRef, mChildGenre, mChildNom, mChildPrenom, mChildHabitation, mChildBoulot;
    Button bValider;
    String[] strChoixSpinner={"Monsieur","Madame", "Demoiseau", "Demoiselle"};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_perso);
        bValider = findViewById(R.id.btn_valider);

        // Read datas from the database

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //email address and uid
            String email = user.getEmail();
            uid = user.getUid();

            Log.d("email", email);
            Log.d("uid", uid);
        }
        else{
            uid = "Unknown user";
            Log.d(TAG,"No user signed in");
        }

        // récupère le path de la database de firebase
        mRootRef = FirebaseDatabase.getInstance().getReference();
        if (mRootRef != null) {

            // Créer l'arbre "Users" de la base de données
            mUserRef = mRootRef.child("Users");
            mIDRef = mUserRef.child(uid);

            // Créer l'arbre "Informations personnelles" de la base de données
            mInfoPersoRef = mIDRef.child("Informations Personnelles");
            mChildGenre = mInfoPersoRef.child("Genre");
            mChildNom = mInfoPersoRef.child("Nom");
            mChildPrenom = mInfoPersoRef.child("Prenom");
            mChildHabitation = mInfoPersoRef.child("Lieu de domicile");
            mChildBoulot = mInfoPersoRef.child("Lieu de travail");

            // Listener des différentes values
            mChildGenre.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    genreDatabasase = dataSnapshot.getValue(String.class);
                    if (genreDatabasase != null) {
                        Log.v("Le genre database est ", genreDatabasase);

                        //cherche la position de genre dans la liste du spinner
                        int position=0;
                        // on incrémente i jusqu'a ce qu'un élément de la liste du spinner soit le même que le genre de la base de donnée
                        for (int i = 0; i<strChoixSpinner.length; i++)
                        {
                            if (genreDatabasase.equals(strChoixSpinner[i])){
                                position = i;
                            }
                        }

                        // on set l'élément correspondant comme premier élément du spinner
                        spinnerMrMme.setSelection(position);
                        spinnerMrMme.invalidate();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.");
                }
            });

            /*---
                    Listeners des informations de l'users pour récupérer ses infos (si déjà remplis)
            ---*/
            // listener du nom de l'user
            mChildNom.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    nomDatabase = dataSnapshot.getValue(String.class);
                    if (nomDatabase != null) {
                        Log.v("Le nom database est ", nomDatabase);
                        editTextNom.setText(nomDatabase);
                        editTextNom.invalidate();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.");
                }
            });

            // listener du prénom de l'user
            mChildPrenom.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    prenomDatabase = dataSnapshot.getValue(String.class);
                    if (prenomDatabase != null) {
                        Log.v("Le prenom database est ", prenomDatabase);
                        editTextPrenom.setText(prenomDatabase);
                        editTextPrenom.invalidate();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.");
                }
            });

            // listener du lieu de domicile de l'user
            mChildHabitation.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    lieuHabitatDatabase = dataSnapshot.getValue(String.class);
                    if (lieuHabitatDatabase != null) {
                        Log.v("L'habitat database est ", lieuHabitatDatabase);
                        editTextHabitat.setText(lieuHabitatDatabase);
                        editTextHabitat.invalidate();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.");
                }
            });

            // listener du lieu de travail de l'user
            mChildBoulot.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    lieuBoulotDatabase = dataSnapshot.getValue(String.class);
                    if (lieuBoulotDatabase != null) {
                        Log.v("Boulot database est ", lieuBoulotDatabase);
                        editTextBoulot.setText(lieuBoulotDatabase);
                        editTextBoulot.invalidate();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.");
                }
            });
        }
        else{}

        //Récuperer le nom et prénom
        editTextNom = findViewById (R.id.nomText);
        editTextPrenom = findViewById (R.id.prenomText);

        //Déroulement du menu déroulant
        spinnerMrMme = findViewById(R.id.menuDeroulantMrMme);
        ArrayAdapter<String> dataAdapterMrMme = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,strChoixSpinner);
        dataAdapterMrMme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMrMme.setAdapter(dataAdapterMrMme);

        //Stockage du genre Mr / Mme
        spinnerMrMme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                strMrMme = String.valueOf(spinnerMrMme.getSelectedItem());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        // récupération des lieux de domicile et de travail
        editTextHabitat = findViewById (R.id.lieuHabitatText);
        editTextBoulot = findViewById (R.id.lieuBoulotText);

        // met en place un click listener sur le bouton valider
        bValider.setOnClickListener(bValiderListener);
    }

    // validation et stockage des informations
    private View.OnClickListener bValiderListener = new View.OnClickListener() {
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

            // Retourne sur le main
            finish();

        }
    };
}
