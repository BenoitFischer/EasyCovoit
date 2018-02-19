package fr.toulon.seatech.easycovoit;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;


public class RechercheTrajetActivity extends AppCompatActivity {

    private static final String TAG = "";

    Button btnRecherche;
    EditText editTextDepart, editTextArrivee, editTextDate;
    String strDate, strDepart, strArrivee, strNbPlacesRestantes;
    String strDepartRecherche ="Pas de départ indiqué", strArriveRecherche;
    int mAnnee, mMois, mJour;
    long nbTrajetExistants;
    DatabaseReference refTrajet, refDateTrajet;
    ArrayList listTrajetDepart, listTrajetArrive, listNbPlaces, listTrajetTrouve;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_trajet);

        refTrajet =  FirebaseDatabase.getInstance().getReference().child("Trajet Date");

        btnRecherche = findViewById(R.id.btnRechercherTrajet);
        editTextDepart = findViewById(R.id.edDepart);
        editTextArrivee = findViewById(R.id.edArrivee);
        editTextDate = findViewById(R.id.edDate);

        editTextDepart.setOnClickListener(onClickDepartListener);
        editTextArrivee.setOnClickListener(onClickArriveListener);
        editTextDate.setOnClickListener(onClickDateListener);
        btnRecherche.setOnClickListener(onClickBouton);

    }

    // Listener pour récuperer le lieu de départ
    private View.OnClickListener onClickDepartListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            editTextDepart = findViewById (R.id.edDepart);
            strDepartRecherche = editTextDepart.getText().toString().toLowerCase();
        }
    };

    // Listener pour récuperer le lieu d'arrivée
    private View.OnClickListener onClickArriveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            editTextArrivee = findViewById (R.id.edArrivee);
            strArriveRecherche = editTextArrivee.getText().toString().toLowerCase();
        }
    };

    // Listener pour récuperer la date
    private View.OnClickListener onClickDateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Toast : Remplir les champs svp
            if((strDepartRecherche == null) || (strArriveRecherche == null) ){

                Context context = getApplicationContext();
                CharSequence text = "Vous devez remplir les champs Départ et Arrivé Avant de choisir la date !";
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(context, text, duration).show();
                return;
            }
            // si les champs sont bien remplis
            else{
                datePicker();
            }

        }

    };

    // Listener pour effectuer la recherche
    private View.OnClickListener onClickBouton = new View.OnClickListener() {

        @Override
        public void onClick(View view){


            // Toast : Remplir les champs svp
            if((strDepartRecherche == null) || (strArriveRecherche == null) || (strDate == null) ){

                Context context = getApplicationContext();
                CharSequence text = "Vous devez remplir les champs pour lancer une recherche !";
                int duration = Toast.LENGTH_SHORT;

                Toast.makeText(context, text, duration).show();
                return;
            }

            else {

                // instanciation de la list des trajets trouvés
                listTrajetTrouve = new ArrayList<String>((int) nbTrajetExistants);

                // pour le nombre de trajets trouvé à cette date (nombre maximum de résultat possible)
                for (int i = 0; i < (int) nbTrajetExistants + 1; i++) {
                    // si les list on encore des éléments a la i eme place
                    if (i < listNbPlaces.size() && i < listTrajetArrive.size() && i < listTrajetDepart.size()) {
                        //Si il y a de la place dans le trajet, si le départ correspond et si l'arrivée correspond
                        if (listNbPlaces.get(i).equals(listTrajetArrive.get(i))
                                && listNbPlaces.get(i).equals(listTrajetDepart.get(i))) {
                            // ajout de l'élement dans la list des trajet trouvés
                            listTrajetTrouve.add(String.valueOf(i + 1));
                            Log.v("élément list", String.valueOf(listTrajetTrouve.get(i)));
                        }
                    }
                }

                // Toast : Résultat(s) trouvé(s) !
                if (listTrajetTrouve.isEmpty() == false) {
                    Context context = getApplicationContext();
                    CharSequence text = "Nous avons trouvé des trajets qui pourraient vous intéressez !";
                    int duration = Toast.LENGTH_SHORT;

                    Toast.makeText(context, text, duration).show();

                    //Ouvre l'activity en transferant la date et la liste des trajets trouvés
                    Intent intent = new Intent(RechercheTrajetActivity.this, ResultatRechercheTrajetActivity.class);
                    intent.putExtra("Date", strDate);
                    intent.putExtra("listTrajet", listTrajetTrouve);
                    startActivity(intent);
                }

                // Toast : Aucun résultat
                if (listTrajetTrouve.isEmpty() == true) {
                    Context context = getApplicationContext();
                    CharSequence text = "Aucun résultat ne correspond à votre recherche !";
                    int duration = Toast.LENGTH_SHORT;

                    Toast.makeText(context, text, duration).show();
                    return;
                }
            }
        }

    };


    private void rechercheTrajet(){

        // j'instancie un tableau de taille équivalente au nombre de trajet existant pour qu'il y pas de soucis de taille
        // Ce tableau va récupérer tout les numéros de trajet avec le même lieu de départ que celui recherché
        listTrajetDepart = new ArrayList((int)nbTrajetExistants);
        // Ce tableau va récupérer tout les numéros de trajet avec le même lieu d'arrivé que celui recherché
        listTrajetArrive = new ArrayList((int)nbTrajetExistants);
        // Ce tableau va récupérer les places restantes par trajet
        listNbPlaces = new ArrayList((int)nbTrajetExistants);

        //Pour le nombre de trajet existant à cette date
        for (int i = 1; i < (int) nbTrajetExistants+1; i++) {

            // je crée un compteur final équivalent à i pour qu'il puisse être utilisé dans le onDataChange
            final int numéroTrajet = i;


            // récupération du lieu de départ du trajet
            refTrajet.child(strDate).child("Trajet " + String.valueOf(i)).child("Informations du trajet").child("Lieu de départ").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    strDepart = dataSnapshot.getValue(String.class).toLowerCase();

                    //Si le lieu de départ rechérché est le même que le lieu de départ du trajet numéro i
                    //Je stocke le numéro du trajet dans le tableau
                    if (strDepart.equals(strDepartRecherche)) {
                        listTrajetDepart.add(String.valueOf(numéroTrajet));
                        Log.v("Trajet correspondant ", "trouvé ! Trajet numéro"+ String.valueOf(numéroTrajet) );
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // récupération du lieu d'arrivé du trajet
            refTrajet.child(strDate).child("Trajet " + String.valueOf(i)).child("Informations du trajet").child("Lieu d'arrivée").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    strArrivee = dataSnapshot.getValue(String.class).toLowerCase();

                    //Si le lieu d'arrivé rechérché est le même que le lieu d'arrivé du trajet numéro i
                    //Je stocke le numéro du trajet dans le tableau
                    if (strArrivee.equals(strArriveRecherche)) {
                        listTrajetArrive.add(String.valueOf(numéroTrajet));
                        Log.v("Trajet correspondant ", "trouvé ! Trajet numéro" + String.valueOf(numéroTrajet));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.");
                }
            });

            // récupération du nombre de places restantes du trajet
            refTrajet.child(strDate).child("Trajet " + String.valueOf(i)).child("Informations du trajet").child("Nombre de places restantes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    strNbPlacesRestantes = dataSnapshot.getValue(String.class);

                    //Si le nombre de place restante du trajet numéro i est non nul
                    //Je stocke le numéro du trajet dans le tableau
                    if (!strNbPlacesRestantes.equals("0")) {
                        listNbPlaces.add(String.valueOf(numéroTrajet));
                        Log.v("Trajet correspondant ", "trouvé ! Trajet numéro"+ String.valueOf(numéroTrajet) );
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.");
                }
            });


        }

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
                        refDateTrajet = FirebaseDatabase.getInstance().getReference().child("Trajet Date").child(strDate);
                        refDateTrajet.addValueEventListener(new ValueEventListener() {
                            @Override

                            public void onDataChange(DataSnapshot dataSnapshot) {

                                // Récupère le nombre de trajet correspondants à la date
                                setNbTrajetExistants(dataSnapshot.getChildrenCount());
                                Log.v("nb de trajet à date est", String.valueOf(nbTrajetExistants));

                                rechercheTrajet();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });
                    }
                }, mAnnee, mMois, mJour);

        datePickerDialog.show();

    }

    public void setNbTrajetExistants(long nbTrajet) {
        this.nbTrajetExistants = nbTrajet;
    }

}