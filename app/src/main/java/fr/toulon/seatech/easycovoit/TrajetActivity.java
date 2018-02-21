package fr.toulon.seatech.easycovoit;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrajetActivity extends AppCompatActivity {

    String strDate;
    ArrayList<String> infoTrajet, infoConducteur;
    int numTrajet;
    public DatabaseReference refTrajet;
    private static final String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajet);

        setTitle("Détails du trajet correspondant");

        //Get le bundle envoyé par l'intent qui appelle cette activité
        Bundle bundle = getIntent().getExtras();
        strDate = bundle.getString("strDate");
        Log.i("date", strDate);
        infoTrajet = bundle.getStringArrayList("infoTrajet");
        numTrajet = bundle.getInt("numTrajet");
        // récupère la database de firebase
        refTrajet =  FirebaseDatabase.getInstance().getReference().child("Trajet Date");
        Button reserver = (Button) findViewById(R.id.btnReserver);
        reserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        donneesTrajet(new MyCallBack() {
            @Override
            public void onCallback(ArrayList<ArrayList<String>> value) {

            }

            @Override
            public void TrajetActivityCallback(ArrayList<String> value) {

                TextView identite = (TextView) findViewById(R.id.tvIdentiteChauffeur);
                TextView depart = (TextView) findViewById(R.id.tvDepart);
                TextView arrivee = (TextView) findViewById(R.id.tvArrivee);
                TextView heure =  (TextView) findViewById(R.id.tvHeure);
                TextView nbPlace = (TextView) findViewById(R.id.tvNbPlace);

                String premiereLettre = "";
                premiereLettre = value.get(3).substring(0, 1);
                String genre = getGenre(value.get(0));
                String conducteur = genre + " " + premiereLettre + ". " + value.get(2);
                identite.setText(conducteur);
                depart.setText(infoTrajet.get(2));
                arrivee.setText(infoTrajet.get(1));
                heure.setText(infoTrajet.get(0));
                nbPlace.setText(infoTrajet.get(3));

            }
        });
    }

    private void donneesTrajet(final MyCallBack myCallback) {

        refTrajet.child(strDate).child("Trajet " + numTrajet).child("Informations du conducteur").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> iConducteur = new ArrayList<String>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    iConducteur.add(ds.getValue(String.class));
                }
                myCallback.TrajetActivityCallback(iConducteur);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });
    }

    private String getGenre(String g){
        String genre = "";
        if(g.equals("Monsieur") || g.equals("Demoiseau")){
            genre = "M";
        }
        else if(g.equals("Madame")){
            genre = "Mme";
        }
        else if(g.equals("Mademoiselle")){
            genre = "Mlle";
        }
        return genre;
    }
}
