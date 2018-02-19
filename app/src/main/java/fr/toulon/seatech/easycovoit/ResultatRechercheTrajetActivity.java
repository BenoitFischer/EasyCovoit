package fr.toulon.seatech.easycovoit;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ResultatRechercheTrajetActivity extends AppCompatActivity{

    private static final String TAG = "";

    public String strDate;
    public ArrayList listTrajetTrouve;
    public int tailleList;
    public DatabaseReference refTrajet;
    ArrayList<ArrayList<String>> collectionTrajet = new ArrayList<ArrayList<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat_recherche_trajet);
        setTitle("Résultats de trajet correspondant");

        //Get le bundle envoyé par l'intent qui appelle cette activité
        Bundle bundle = getIntent().getExtras();
        strDate = bundle.getString("Date");
        Log.i("date", strDate);
        listTrajetTrouve = bundle.getStringArrayList("listTrajet");
        tailleList = listTrajetTrouve.size();
        Log.i("date", String.valueOf(tailleList));

        // récupère la database de firebase
        refTrajet =  FirebaseDatabase.getInstance().getReference().child("Trajet Date");

        //Appel à la récupération des données de FireBase et création de layouts dynamiquement
        donneesTrajet(new MyCallBack(){
            @Override
            public void onCallback(ArrayList<ArrayList<String>> value) {
                createLayoutsResultats(value);
            }

            @Override
            public void TrajetActivityCallback(ArrayList value) {
            }
        });

    }

    private void donneesTrajet(final MyCallBack myCallback) {
        int tailleList = listTrajetTrouve.size();

        for (int i=0;i<tailleList;++i) {
            refTrajet.child(strDate).child("Trajet " + String.valueOf(listTrajetTrouve.get(i))).child("Informations du trajet").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> infoTrajet = new ArrayList<String>();

                    int j = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        infoTrajet.add(ds.getValue(String.class));
                        //if (infoTrajet.get(j) != null) {
                            Log.i("1 1", infoTrajet.get(j));
                        //}
                        ++j;
                    }
                    collectionTrajet.add(infoTrajet);
                    myCallback.onCallback(collectionTrajet);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.");
                }
            });
        }
    }

    private void createLayoutsResultats(ArrayList<ArrayList<String>> collectionTrajet) {

        LinearLayout principalLayoutVertical =(LinearLayout)findViewById(R.id.linLayoutResultat);
        principalLayoutVertical.setGravity(Gravity.CENTER);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout layoutVertical = new LinearLayout(this);
        layoutVertical.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        layoutVertical.setOrientation(LinearLayout.VERTICAL);
        layoutVertical.setGravity(Gravity.CENTER);

        scrollView.addView(layoutVertical);

        for (int i = 0; i < tailleList; ++i)
        {
            final ArrayList<String> trajet = collectionTrajet.get(0);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke(3, Color.BLACK);
            drawable.setCornerRadius(2);
            drawable.setColor(Color.LTGRAY);

            RelativeLayout layoutHorizontal = new RelativeLayout(this);
            layoutHorizontal.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            //layoutVertical.setOrientation(LinearLayout.HORIZONTAL);
            layoutHorizontal.setBackgroundDrawable(drawable);
            layoutHorizontal.setGravity(Gravity.CENTER);

            Button b = new Button(this);
            b.setId(i);
            b.setText("Button "+i+1);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Ouvre l'activity en transferant la date et la liste des trajets trouvés
                    Intent intent = new Intent(ResultatRechercheTrajetActivity.this, TrajetActivity.class);
                    intent.putExtra("Date", strDate);
                    intent.putExtra("InfoTrajet", trajet);
                    startActivity(intent);
                }
            });

            TextView tv1 = new TextView(this);
            tv1.setText("Trajet "+ listTrajetTrouve.get(i).toString());
            TextView tv2 = new TextView(this);
            tv2.setText("Lieu de départ : " + trajet.get(2));
            tv1.setId('2');
            TextView tv3 = new TextView(this);
            tv3.setText("Lieu de d'arrivée : " + trajet.get(1));
            TextView tv4 = new TextView(this);
            tv4.setText("Date : " + strDate);
            TextView tv5 = new TextView(this);
            tv5.setText("Heure : " + trajet.get(0));
            layoutHorizontal.addView(tv1);
            layoutHorizontal.addView(tv2);
            layoutHorizontal.addView(tv3);
            layoutHorizontal.addView(tv4);
            layoutHorizontal.addView(b);
            layoutVertical.addView(layoutHorizontal);
        }
        principalLayoutVertical.addView(scrollView);
    }

}
