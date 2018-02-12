package fr.toulon.seatech.easycovoit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class PropositionTrajet extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "";

    int mAnnee, mMois, mJour, mHeure, mMinute;
    long nbTrajetExistants;

    EditText DateHeure, LieuDepart, LieuArrivee, NbPlacePropose;
    Button btnProposer;

    String uid, strGenreCovoitureur, strNomCovoitureur, strPrenomCovoitureur, strLieuDepart, strLieuArrivee, strDate, strHeure, strNbPlacePropose;

    DatabaseReference mRootRef, mTrajetRef, mIDTrajet, mGenreCovoitureur , mIDCovoitureur , mPrenomCovoitureur, mNomCovoitureur, mDateRef, mHeureRef, mLieuDepartRef, mLieuArriveeRef, mNbPlacePropose;

    DatabaseReference mInfoTrajetRef, mInfoConducteurRef, mInfoPassagerRef;

    DatabaseReference currentUserNameDatabase, currentUserLastNameDatabase, currentUserGenreDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposition_trajet);

        DateHeure = (EditText) findViewById(R.id.editDate);
        LieuDepart = (EditText) findViewById(R.id.editLieuDepart);
        LieuArrivee = (EditText) findViewById(R.id.editLieuArrivee);
        NbPlacePropose = (EditText) findViewById(R.id.editNbPlacePropose);
        btnProposer = (Button) findViewById(R.id.btnProposer);
        DateHeure.setOnClickListener(edDateHeure);
        btnProposer.setOnClickListener(this);


        FirebaseDatabase.getInstance().getReference().child("Trajet").addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {

                setNbTrajetExistants(dataSnapshot.getChildrenCount());
                Log.v("datasnapshot:", String.valueOf(nbTrajetExistants));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

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


        currentUserNameDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Prenom");
        //trajetDataBase

        currentUserNameDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                strPrenomCovoitureur = dataSnapshot.getValue(String.class);
                if(strPrenomCovoitureur!=null){
                    Log.v("prenom conducteur:", strPrenomCovoitureur);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        currentUserLastNameDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Nom");

        currentUserLastNameDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                strNomCovoitureur = dataSnapshot.getValue(String.class);
                if(strNomCovoitureur != null){
                    Log.v("nom conducteur:", strNomCovoitureur);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        currentUserGenreDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Genre");

        currentUserGenreDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                strGenreCovoitureur = dataSnapshot.getValue(String.class);
                if(strGenreCovoitureur != null){
                    Log.v("Genre conducteur:", strGenreCovoitureur);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

    }

    private View.OnClickListener edDateHeure = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            datePicker();
        }
    };

    @Override
    public void onClick(View v) {

        //-- Stockage d'une proposition de trajet

        //Stockage du lieu de départ, du lieu de d'arrivée, du nombre de place proposé
        strLieuDepart = LieuDepart.getText().toString();
        strLieuArrivee = LieuArrivee.getText().toString();
        strNbPlacePropose = NbPlacePropose.getText().toString();

        int nbPlacePropose = Integer.parseInt(NbPlacePropose.getText().toString());

        mRootRef = FirebaseDatabase.getInstance().getReference();
        if(mRootRef !=null){
            mTrajetRef = mRootRef.child("Trajet");

            if(getNbTrajetExistants()==0){
                Log.v("nbTrajetsExistants==0:", String.valueOf(getNbTrajetExistants()));
                mIDTrajet = mTrajetRef.child("Trajet " + String.valueOf(1));

                mInfoTrajetRef = mIDTrajet.child("Informations du trajet");
                mLieuDepartRef = mInfoTrajetRef.child("Lieu de départ");
                mLieuArriveeRef = mInfoTrajetRef.child("Lieu d'arrivée");
                mDateRef = mInfoTrajetRef.child("Date");
                mHeureRef =  mInfoTrajetRef.child("Heure");
                mNbPlacePropose = mInfoTrajetRef.child("Nombre de places restantes");


                mInfoConducteurRef = mIDTrajet.child("Informations du conducteur");
                mIDCovoitureur = mInfoConducteurRef.child("ID");
                mGenreCovoitureur = mInfoConducteurRef.child("Genre");
                mNomCovoitureur = mInfoConducteurRef.child("Nom");
                mPrenomCovoitureur = mInfoConducteurRef.child("Prenom");


                mInfoPassagerRef = mIDTrajet.child("Informations de passagers");

                for(int i=1;i<=nbPlacePropose;++i) {
                    mInfoConducteurRef = mInfoPassagerRef.child("Passager " + String.valueOf(i));
                    DatabaseReference mIDPassager = mInfoConducteurRef.child("ID");
                    DatabaseReference mGenrePassager = mInfoConducteurRef.child("Genre");
                    DatabaseReference mNomPassager = mInfoConducteurRef.child("Nom");
                    DatabaseReference mPrenomPassager = mInfoConducteurRef.child("Prenom");
                    mIDPassager.setValue("");
                    mGenrePassager.setValue("");
                    mNomPassager.setValue("");
                    mPrenomPassager.setValue("");
                }
            }
            else if(nbTrajetExistants>0){
                Log.v("nbTrajetsExistants>0:", String.valueOf(nbTrajetExistants));
                mIDTrajet = mTrajetRef.child("Trajet " + String.valueOf(++nbTrajetExistants));
                Log.v("nbTrajetsExistants>0++:", String.valueOf(nbTrajetExistants));

                mInfoTrajetRef = mIDTrajet.child("Informations du trajet");
                mLieuDepartRef = mInfoTrajetRef.child("Lieu de départ");
                mLieuArriveeRef = mInfoTrajetRef.child("Lieu d'arrivée");
                mDateRef = mInfoTrajetRef.child("Date");
                mHeureRef =  mInfoTrajetRef.child("Heure");
                mNbPlacePropose = mInfoTrajetRef.child("Nombre de places restantes");


                mInfoConducteurRef = mIDTrajet.child("Informations du conducteur");
                mIDCovoitureur = mInfoConducteurRef.child("ID");
                mGenreCovoitureur = mInfoConducteurRef.child("Genre");
                mNomCovoitureur = mInfoConducteurRef.child("Nom");
                mPrenomCovoitureur = mInfoConducteurRef.child("Prenom");


                mInfoPassagerRef = mIDTrajet.child("Informations de passagers");

                for(int i=1;i<=nbPlacePropose;++i) {
                    mInfoConducteurRef = mInfoPassagerRef.child("Passager " + String.valueOf(i));
                    DatabaseReference mIDPassager = mInfoConducteurRef.child("ID");
                    DatabaseReference mGenrePassager = mInfoConducteurRef.child("Genre");
                    DatabaseReference mNomPassager = mInfoConducteurRef.child("Nom");
                    DatabaseReference mPrenomPassager = mInfoConducteurRef.child("Prenom");
                    mIDPassager.setValue("");
                    mGenrePassager.setValue("");
                    mNomPassager.setValue("");
                    mPrenomPassager.setValue("");

                }
            }
        }

        Log.v("nbTrajetsExistants:", String.valueOf(getNbTrajetExistants()));


        // Ecriture dans la base de donnée
        mIDCovoitureur.setValue(uid);
        mPrenomCovoitureur.setValue(strPrenomCovoitureur);
        mNomCovoitureur.setValue(strNomCovoitureur);
        mLieuDepartRef.setValue(strLieuDepart);
        mLieuArriveeRef.setValue(strLieuArrivee);
        mDateRef.setValue(strDate);
        mHeureRef.setValue(strHeure);
        mNbPlacePropose.setValue(strNbPlacePropose);
        //Pour voir les valeurs que j'ai récupérées
        Log.v("ID de conducteur", uid);
        Log.v("Prenom de conducteur", strPrenomCovoitureur);
        Log.v("Nom de conducteur", strNomCovoitureur);
        Log.v("Lieu de départ",strLieuDepart);
        Log.v("Lieu d'arrivée",strLieuArrivee);
        Log.v("Date",strDate);
        Log.v("Heure",strDate);
        Log.v("Nb de places restantes",strNbPlacePropose);
        // Retourne sur le main
        Intent intent = new Intent(PropositionTrajet.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    private void datePicker(){

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mAnnee = c.get(Calendar.YEAR);
        mMois = c.get(Calendar.MONTH);
        mJour = c.get(Calendar.DAY_OF_MONTH);

        //DatePickerDialog(Context context, DatePickerDialog.OnDateSetListener listener, int year, int month, int dayOfMonth)
        //Creates a new date picker dialog for the specified date using the parent context's default date picker dialog theme.

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        strDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                    }
                }, mAnnee, mMois, mJour);
        timePicker();
        datePickerDialog.show();
    }

    private void timePicker(){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHeure = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHeure = hourOfDay;
                        mMinute = minute;

                        strHeure = updateTime(hourOfDay, minute);
                        DateHeure.setText(strDate+" "+ strHeure);

                    }
                }, mHeure, mMinute,true);
        timePickerDialog.show();
    }

    private String updateTime(int hours, int mins) {

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        String aTime = new StringBuilder().append(hours).append('H')
                .append(minutes).toString();
        return aTime;
    }

    public long getNbTrajetExistants() {return nbTrajetExistants;}

    public void setNbTrajetExistants(long nbTrajetExistants) {this.nbTrajetExistants = nbTrajetExistants;}


}