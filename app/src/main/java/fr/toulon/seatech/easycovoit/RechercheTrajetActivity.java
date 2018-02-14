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

import static android.opengl.Matrix.length;
import static java.lang.Thread.sleep;

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
    String strDepartRecherche ="Pas de départ indiqué", strArriveRecherche, strDateRecherche;
    int mAnnee, mMois, mJour;
    long nbTrajetExistants;
    DatabaseReference refTrajet, refDateTrajet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_trajet);

        refTrajet =  FirebaseDatabase.getInstance().getReference().child("Trajet Date");

        btnRcherche = findViewById(R.id.btnRechercherTrajet);

        editTextDepart = (EditText) findViewById(R.id.edDepart);
        editTextArrivee = (EditText) findViewById(R.id.edArrivee);
        editTextDate = (EditText) findViewById(R.id.edDate);

        editTextDepart.setOnClickListener(onClickArriveListener);
        editTextDate.setOnClickListener(onClickDateListener);

        btnRcherche.setOnClickListener(onClickBouton);

        //setNbTrajetExistants();





    }


    public void onClick(View view) {

        Log.v (" ########", "Je suis ici");
        ViewGroup groupLayout = (ViewGroup)view.getParent();
        ScrollView scroll = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView tvConducteur = new TextView(this);
        tvConducteur.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvConducteur.setText(strGenreConducteur + " " + strPrenomConducteur + " " +strNomConducteur);
        groupLayout.addView(tvConducteur);


        scroll.addView(layout);
        setContentView(scroll);
    }

    /*
    // Récupération du nombre de trajet par date
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

*/

    private View.OnClickListener onClickArriveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            strDepartRecherche = editTextDepart.getText().toString();
            Log.v("lieu départ recherché", strDepartRecherche);
        }
    };

    private View.OnClickListener onClickDateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            datePicker();

        }
    };

    private View.OnClickListener onClickBouton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            String[][] strTableauInfoTrajet = tableauInfoTrajet();

            for(int i = 0; i<nbTrajetExistants; i++)
            {
                Log.v("Trajet"+String.valueOf(i+1),"Départ est "+strTableauInfoTrajet[i][0]);

                if (strTableauInfoTrajet[i][0] == strDepartRecherche)
                {
                    Log.v("Vous avez trouvé ", "hourra !!!!");
                }
                else{
                    Log.v("Il ne correspond pas", "Bouuuh !!!!");
                }
            }



        }

    };


    private String[][] tableauInfoTrajet() {
        Log.v("Nb trajet (valeur de i)", String.valueOf(nbTrajetExistants));
        // pour chaque trajet existant à la date demandée

        String[][] strInfoTrajet = new String [(int) nbTrajetExistants][7];

        for (int i = 0; i < nbTrajetExistants; ++i) {
            strInfoTrajet[i] = donneesTrajet(i, strDate);
        }
        return strInfoTrajet;
    }

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
                        strDate = dayOfMonth + "-" + (monthOfYear + 1 ) + "-" + year;
                        editTextDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        Log.v ("La date recherchée est ",strDate);
                        refDateTrajet = FirebaseDatabase.getInstance().getReference().child("Trajet Date").child(strDate);
                        refDateTrajet.addValueEventListener(new ValueEventListener() {
                            @Override

                            public void onDataChange(DataSnapshot dataSnapshot) {

                                // Récupère le nombre de trajet correspondants à la date
                                setNbTrajetExistants(dataSnapshot.getChildrenCount());
                                Log.v("nb de trajet à date est", String.valueOf(nbTrajetExistants));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });
                    }
                }, mAnnee, mMois, mJour);

        datePickerDialog.show();

    }

    // Fonction récupérant les données d'un trajet à une date donnée
    private String[] donneesTrajet(int numeroTrajet, String strDate) {

        numeroTrajet = numeroTrajet +1; // les trajets sont triées à partir de 1, les i commencent à 0
        final String[] infosTrajet = new String[7];

        refTrajet.child(strDate).child("Trajet " + String.valueOf(numeroTrajet)).child("Informations du trajet").child("Lieu de départ").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strDepart = dataSnapshot.getValue(String.class);
                if (strDepart != null) {

                    infosTrajet[0] = strDepart;
                    Log.v("Depart ",  infosTrajet[0]);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        refTrajet.child(strDate).child("Trajet " + String.valueOf(numeroTrajet)).child("Informations du trajet").child("Lieu d'arrivée").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strArrivee = dataSnapshot.getValue(String.class);
                if (strArrivee != null) {
                    Log.v("Arrivee ", strArrivee);
                    infosTrajet[1] = strArrivee;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        refTrajet.child(strDate).child("Trajet " + String.valueOf(numeroTrajet)).child("Informations du trajet").child("Heure").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strHeure = dataSnapshot.getValue(String.class);
                if (strHeure != null) {
                    Log.v("Heure ", strHeure);
                    infosTrajet[2] = strHeure;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        refTrajet.child(strDate).child("Trajet " + String.valueOf(numeroTrajet)).child("Informations du trajet").child("Nombre de places restantes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strNbPlacesRestantes = dataSnapshot.getValue(String.class);
                if (strNbPlacesRestantes != null) {
                    Log.v("NbPlacesRestantes ", strNbPlacesRestantes);
                    infosTrajet[3] = strNbPlacesRestantes;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        refTrajet.child(strDate).child("Trajet " + String.valueOf(numeroTrajet)).child("Informations du conducteur").child("Genre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strGenreConducteur = dataSnapshot.getValue(String.class);
                if (strGenreConducteur != null) {
                    Log.v("Genre conducteur ", strGenreConducteur);
                    infosTrajet[4] = strGenreConducteur;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        refTrajet.child(strDate).child("Trajet " + String.valueOf(numeroTrajet)).child("Informations du conducteur").child("Nom").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strNomConducteur = dataSnapshot.getValue(String.class);
                if (strNomConducteur != null) {
                    Log.v("Nom conducteur ", strNomConducteur);
                    infosTrajet[5] = strNomConducteur;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

        refTrajet.child(strDate).child("Trajet " + String.valueOf(numeroTrajet)).child("Informations du conducteur").child("Prenom").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strPrenomConducteur = dataSnapshot.getValue(String.class);
                if (strPrenomConducteur != null) {
                    Log.v("Prénom du conducteur ", strPrenomConducteur);
                    infosTrajet[6] = strPrenomConducteur;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });
        Log.v("Info trajet", "est"+infosTrajet[0]);
        return infosTrajet;
    }


    public long getNbTrajetExistants() {
        return nbTrajetExistants;
    }

    public void setNbTrajetExistants(long nbTrajet) {
        this.nbTrajetExistants = nbTrajet;
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