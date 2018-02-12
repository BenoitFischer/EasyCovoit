package fr.toulon.seatech.easycovoit;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

/*
Trajet
	date
		trajet1
			informations de trajet
				-de
				=ou
				-heure
				-date
			informations de conducteur
				-genre
				-
				-
			information de passagers
				-passager1
					-genre
					-nom
					-age
				-passager2
					-genre
					-nom
					-age
*/
public class RechercheTrajetActivity extends AppCompatActivity  implements View.OnClickListener {

    private static final String TAG = "";

    Button btnRcherche;
    EditText editTextDepart, editTextArrivee, editTextDate;
    String strDate, strDepart, strArrivee, strHeure, strGenreConducteur, strNomConducteur, strPrenomConducteur, strNbPlacesRestantes, strDateTrajetListener;
    int mAnnee, mMois, mJour;
    long nbTrajetExistants;
    DatabaseReference refTrajet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_trajet);

        refTrajet =  FirebaseDatabase.getInstance().getReference().child("Trajet");

        btnRcherche = new Button(this);

        editTextDepart = (EditText) findViewById(R.id.edDepart);
        editTextArrivee = (EditText) findViewById(R.id.edArrivee);
        editTextDate = (EditText) findViewById(R.id.edDate);

        editTextDate.setOnClickListener(edDateListener);

        btnRcherche.setOnClickListener(this);

        //setNbTrajetExistants();

        refTrajet.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("##########:", "data changed");
                nbTrajetExistants = dataSnapshot.getChildrenCount();

                String strMatchedIDTrajet[];
                Log.v("Nb trajet existant:", String.valueOf(nbTrajetExistants));
                // pour chaque trajet existant, verifier si la date correspond à la date de la recherche et stocker le nom du trajet
                for (int i = 0; i < nbTrajetExistants; ++i) {
                    strDate = getDateTrajet(i+1);
                    Log.v("Date trajet" + String.valueOf(i+1) +" : ", strDate+"");
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    public void onClick(View view) {

        ViewGroup groupLayout = (ViewGroup)view.getParent();
        ScrollView scroll = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);


        // pour chaque trajet existant à la date demandée
        for (int i = 0; i < nbTrajetExistants; ++i) {

            donneesTrajet(i, strDate);
            TextView tvConducteur = new TextView(this);
            tvConducteur.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvConducteur.setText(strGenreConducteur + " " + strPrenomConducteur + " " +strNomConducteur);
            groupLayout.addView(tvConducteur);

        }
        scroll.addView(layout);
        setContentView(scroll);
    }

    private View.OnClickListener edDateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            datePicker();
        }
    };

    private void datePicker() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        // set le champ de la date à la date actuelle
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
                        editTextDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        FirebaseDatabase.getInstance().getReference().child("TrajetHeure").child(strDate);
                       /* FirebaseDatabase.getInstance().getReference().child("TrajetHeure").child(strDate).addValueEventListener(new ValueEventListener() {
                            @Override

                            public void onDataChange(DataSnapshot dataSnapshot) {

                                // Récupère le nombre de trajet correspondants à la date
                                setNbTrajetExistants(dataSnapshot.getChildrenCount());
                                Log.v("!!!!!#######!!!!!", String.valueOf(nbTrajetExistants));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });*/
                    }
                }, mAnnee, mMois, mJour);
        datePickerDialog.show();
    }

    // Fonction récupérant les données d'un trajet à une date donnée
    private void donneesTrajet(int numeroTrajet, String strDate) {

        FirebaseDatabase.getInstance().getReference().child("TrajetHeure").child(strDate).child("Trajet " + String.valueOf(numeroTrajet)).child("Informations du trajet").child("Lieu de départ").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strDepart = dataSnapshot.getValue(String.class);
                if (strDepart != null) {
                    Log.v("Depart : ", strDepart);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        FirebaseDatabase.getInstance().getReference().child("TrajetHeure").child(strDate).child("Trajet " + String.valueOf(numeroTrajet)).child("Informations du trajet").child("Lieu d'arrivée").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strArrivee = dataSnapshot.getValue(String.class);
                if (strArrivee != null) {
                    Log.v("Arrivee : ", strArrivee);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        FirebaseDatabase.getInstance().getReference().child("TrajetHeure").child(strDate).child("Trajet " + String.valueOf(numeroTrajet)).child("Informations du trajet").child("Heure").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strHeure = dataSnapshot.getValue(String.class);
                if (strHeure != null) {
                    Log.v("Heure : ", strHeure);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        FirebaseDatabase.getInstance().getReference().child("TrajetHeure").child(strDate).child("Trajet " + String.valueOf(numeroTrajet)).child("Informations du trajet").child("Nombre de places restantes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strNbPlacesRestantes = dataSnapshot.getValue(String.class);
                if (strNbPlacesRestantes != null) {
                    Log.v("NbPlacesRestantes : ", strNbPlacesRestantes);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        FirebaseDatabase.getInstance().getReference().child("TrajetHeure").child(strDate).child("Trajet " + String.valueOf(numeroTrajet)).child("Informations du conducteur").child("Genre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strGenreConducteur = dataSnapshot.getValue(String.class);
                if (strGenreConducteur != null) {
                    Log.v("Genre conducteur : ", strGenreConducteur);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        FirebaseDatabase.getInstance().getReference().child("TrajetHeure").child(strDate).child("Trajet " + String.valueOf(numeroTrajet)).child("Informations du conducteur").child("Nom").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strNomConducteur = dataSnapshot.getValue(String.class);
                if (strNomConducteur != null) {
                    Log.v("Nom conducteur : ", strNomConducteur);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        FirebaseDatabase.getInstance().getReference().child("TrajetHeure").child(strDate).child("Trajet " + String.valueOf(numeroTrajet)).child("Informations du conducteur").child("Prenom").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strPrenomConducteur = dataSnapshot.getValue(String.class);
                if (strPrenomConducteur != null) {
                    Log.v("Prénom du conducteur : ", strPrenomConducteur);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });
    }


    public long getNbTrajetExistants() {
        return nbTrajetExistants;
    }

    public void setNbTrajetExistants(long nbTrajetExistants) {
        this.nbTrajetExistants = nbTrajetExistants;
    }

    public String getDateTrajet(int i) {
        final String[] str = new String[1];
        Log.v ("i = ",String.valueOf(i));
        Log.v("str[0]", str[0] + "");
        refTrajet.child("Trajet " + String.valueOf(i)).child("Informations du trajet").child("Date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strDateTrajetListener = dataSnapshot.getValue().toString();
                Log.v ("Date du trajet",strDateTrajetListener+"");
                str[0] = strDateTrajetListener;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        // la valeur est retournée avant qu'elle soit modifiée ... ?!
        Log.v("str[0]", str[0] + "");
        return str[0];
    }

}