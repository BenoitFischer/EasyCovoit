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
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.view.ViewGroup.*;

public class ResultatRechercheTrajetActivity extends AppCompatActivity{

    private static final String TAG = "";

    public String strDate;
    public ArrayList listTrajetTrouve;
    public int tailleList;
    public DatabaseReference refTrajet;
    ArrayList<ArrayList<String>> collectionTrajet = new ArrayList<ArrayList<String>>();
    int imageResource;
    public Button[] btnVoirDetails;
    public Button btnModifier;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat_recherche_trajet);

        setTitle("Résultats de trajet correspondant");

        imageResource = R.drawable.arrow;

        //Get le bundle envoyé par l'intent qui appelle cette activité
        Bundle bundle = getIntent().getExtras();
        strDate = bundle.getString("Date");
        Log.i("date", strDate);
        listTrajetTrouve = bundle.getStringArrayList("listTrajet");
        tailleList = listTrajetTrouve.size();
        btnVoirDetails= new Button[tailleList];
        Log.i("date", String.valueOf(tailleList));

        // récupère la database de firebase
        refTrajet =  FirebaseDatabase.getInstance().getReference().child("Trajet Date");

        //Appel à la récupération des données de FireBase et création de layouts dynamiquement
        donneesTrajet(new MyCallBack(){
            @Override
            public void onCallback(ArrayList<ArrayList<String>> value) {

                for (int i = 0; i < tailleList; ++i) {
                    ArrayList<String> trajet = value.get(i);
                    Log.i(String.valueOf(i),trajet.get(0));
                    Log.i(String.valueOf(i),trajet.get(1));
                    Log.i(String.valueOf(i),trajet.get(2));
                    Log.i(String.valueOf(i),trajet.get(3));
                }
                createLayoutsResultats(value);
            }

            @Override
            public void TrajetActivityCallback(ArrayList value) {
            }
        });

    }

    private void donneesTrajet(final MyCallBack myCallback) {

        final int tailleList = listTrajetTrouve.size();

        for (int i=0;i<tailleList;++i) {

            final int finalI = i;
            refTrajet.child(strDate).child("Trajet " + String.valueOf(listTrajetTrouve.get(i))).child("Informations du trajet").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> infoTrajet = new ArrayList<String>();

                    int j = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        infoTrajet.add(ds.getValue(String.class));
                        ++j;
                    }
                    collectionTrajet.add(infoTrajet);
                    if(finalI == tailleList-1)
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
            final ArrayList<String> trajet = collectionTrajet.get(i);
            final int numTrajet = Integer.parseInt((String) listTrajetTrouve.get(i));
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke(3, Color.BLACK);
            drawable.setCornerRadius(2);
            drawable.setColor(Color.TRANSPARENT);

            RelativeLayout layoutHorizontal = new RelativeLayout(this);
            LayoutParams lgLayoutHorizontal = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            layoutVertical.setOrientation(LinearLayout.VERTICAL);
            lgLayoutHorizontal.addRule(Gravity.CENTER_HORIZONTAL);

            layoutHorizontal.setLayoutParams(lgLayoutHorizontal);
            layoutHorizontal.setBackground(drawable);
            layoutHorizontal.setId(i+'4');
            layoutHorizontal.setLayoutParams(lgLayoutHorizontal);


            TextView tv1 = new TextView(this);
            tv1.setText(trajet.get(2) + "  à  " + trajet.get(1));
            tv1.setTextSize(18);
            tv1.setTextColor(Color.BLACK);
            tv1.setId('1');

            LayoutParams lpTv1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            //lpTv1.addRule(RelativeLayout.ALIGN_PARENT_START);
            lpTv1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lpTv1.addRule(RelativeLayout.CENTER_IN_PARENT);
            tv1.setLayoutParams(lpTv1);

            TextView tv2 = new TextView(this);
            tv2.setText("Heure de départ : " + trajet.get(0));
            tv2.setId('2');
            tv2.setTextSize(18);
            tv2.setTextColor(Color.BLACK);
            LayoutParams lpTv2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lpTv2.addRule(RelativeLayout.BELOW, tv1.getId());
            lpTv2.addRule(RelativeLayout.CENTER_IN_PARENT);
            tv2.setLayoutParams(lpTv2);

            TextView tv3 = new TextView(this);
            tv3.setText("Nombre de places restantes : " + trajet.get(3));
            tv3.setId('3');
            tv3.setTextSize(18);
            tv3.setTextColor(Color.BLACK);
            LayoutParams lpTv3 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lpTv3.addRule(RelativeLayout.BELOW, tv2.getId());
            lpTv3.addRule(RelativeLayout.CENTER_IN_PARENT);
            tv3.setLayoutParams(lpTv3);

            btnVoirDetails[i] = new Button(this);
            btnVoirDetails[i].setText("Voir plus de détails");
            btnVoirDetails[i].setId('4');
            btnVoirDetails[i].setTextSize(12);
            btnVoirDetails[i].setTextColor(Color.GRAY);
            LayoutParams lpTv4 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lpTv4.addRule(RelativeLayout.BELOW, tv3.getId());
            lpTv4.addRule(RelativeLayout.CENTER_IN_PARENT);
            btnVoirDetails[i].setLayoutParams(lpTv4);
            btnVoirDetails[i].setOnClickListener(getOnClickTrajetActivity(btnVoirDetails[i], numTrajet, trajet));

            layoutHorizontal.addView(tv1);
            layoutHorizontal.addView(tv2);
            layoutHorizontal.addView(tv3);
            layoutHorizontal.addView(btnVoirDetails[i]);
            layoutVertical.addView(layoutHorizontal);
        }
        btnModifier = new Button(this);
        btnModifier.setId('3');
        btnModifier.setText("Modifier la recherche");
        btnModifier.setTextSize(14);
        btnModifier.setTextColor(Color.BLACK);
        LayoutParams lpBtn = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lpBtn.addRule(RelativeLayout.CENTER_IN_PARENT);
        btnModifier.setLayoutParams(lpBtn);
        btnModifier.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layoutVertical.addView(btnModifier);
        principalLayoutVertical.addView(scrollView);
    }

    View.OnClickListener getOnClickTrajetActivity(final Button btnVoirDetails, final int numTrajet,final ArrayList<String> trajet)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                    Intent intent = new Intent(ResultatRechercheTrajetActivity.this, TrajetActivity.class);
                    intent.putExtra("strDate", strDate);
                    intent.putExtra("numTrajet", numTrajet);
                    intent.putExtra("infoTrajet", trajet);
                    startActivity(intent);
            }
        };
    }
}
