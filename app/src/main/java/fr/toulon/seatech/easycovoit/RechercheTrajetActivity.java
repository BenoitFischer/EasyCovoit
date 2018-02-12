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
    String strDate, strDepart, strArrivee, strHeure, strGenreConducteur, strNomConducteur, strPrenomConducteur, strNbPlacesRestantes;
    int mAnnee, mMois, mJour;
    long nbTrajetExistants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_trajet);


        btnRcherche = new Button(this);

        editTextDepart = (EditText) findViewById(R.id.edDepart);
        editTextArrivee = (EditText) findViewById(R.id.edArrivee);
        editTextDate = (EditText) findViewById(R.id.edDate);

        editTextDate.setOnClickListener(edDateListener);

        btnRcherche.setOnClickListener(this);
    }


    public void onClick(View view) {

        ViewGroup groupLayout = (ViewGroup)view.getParent();
        ScrollView scroll = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
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

        mJour = c.get(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH, mJour + 1);
        //DatePickerDialog(Context context, DatePickerDialog.OnDateSetListener listener, int year, int month, int dayOfMonth)
        //Creates a new date picker dialog for the specified date using the parent context's default date picker dialog theme.

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        strDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        editTextDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        FirebaseDatabase.getInstance().getReference().child("TrajetHeure").child(strDate).addValueEventListener(new ValueEventListener() {
                            @Override

                            public void onDataChange(DataSnapshot dataSnapshot) {

                                setNbTrajetExistants(dataSnapshot.getChildrenCount());
                                Log.v("datasnapshot:", String.valueOf(nbTrajetExistants));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });
                    }
                }, mAnnee, mMois, mJour);
        datePickerDialog.show();
    }

    private void donneesTrajet(int nb, String strDate) {

        FirebaseDatabase.getInstance().getReference().child("TrajetHeure").child(strDate).child("Trajet " + String.valueOf(nb)).child("Informations du trajet").child("Lieu de départ").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strDepart = dataSnapshot.getValue(String.class);
                if (strDepart != null) {
                    Log.v(" Depart:", strDepart);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        FirebaseDatabase.getInstance().getReference().child("TrajetHeure").child(strDate).child("Trajet " + String.valueOf(nb)).child("Informations du trajet").child("Lieu d'arrivée").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strArrivee = dataSnapshot.getValue(String.class);
                if (strArrivee != null) {
                    Log.v(" Arrivee:", strArrivee);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        FirebaseDatabase.getInstance().getReference().child("TrajetHeure").child(strDate).child("Trajet " + String.valueOf(nb)).child("Informations du trajet").child("Heure").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strHeure = dataSnapshot.getValue(String.class);
                if (strHeure != null) {
                    Log.v(" Heure:", strHeure);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        FirebaseDatabase.getInstance().getReference().child("TrajetHeure").child(strDate).child("Trajet " + String.valueOf(nb)).child("Informations du trajet").child("Nombre de places restantes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strNbPlacesRestantes = dataSnapshot.getValue(String.class);
                if (strNbPlacesRestantes != null) {
                    Log.v("NbPlacesRestantes:", strNbPlacesRestantes);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        FirebaseDatabase.getInstance().getReference().child("TrajetHeure").child(strDate).child("Trajet " + String.valueOf(nb)).child("Informations du conducteur").child("Genre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strGenreConducteur = dataSnapshot.getValue(String.class);
                if (strGenreConducteur != null) {
                    Log.v("NbPlacesRestantes:", strGenreConducteur);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        FirebaseDatabase.getInstance().getReference().child("TrajetHeure").child(strDate).child("Trajet " + String.valueOf(nb)).child("Informations du conducteur").child("Nom").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strNomConducteur = dataSnapshot.getValue(String.class);
                if (strNomConducteur != null) {
                    Log.v("NbPlacesRestantes:", strNomConducteur);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        FirebaseDatabase.getInstance().getReference().child("TrajetHeure").child(strDate).child("Trajet " + String.valueOf(nb)).child("Informations du conducteur").child("Prenom").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strPrenomConducteur = dataSnapshot.getValue(String.class);
                if (strPrenomConducteur != null) {
                    Log.v("NbPlacesRestantes:", strPrenomConducteur);
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
}