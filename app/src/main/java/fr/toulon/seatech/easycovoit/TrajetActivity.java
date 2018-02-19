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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TrajetActivity extends AppCompatActivity {

    String strDate;
    ArrayList infoTrajet;
    public DatabaseReference refTrajet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajet);
        setTitle("Détails du trajet correspondant");

        //Get le bundle envoyé par l'intent qui appelle cette activité
        Bundle bundle = getIntent().getExtras();
        strDate = bundle.getString("Date");
        Log.i("date", strDate);
        infoTrajet = bundle.getStringArrayList("infoTrajet");

        // récupère la database de firebase
        refTrajet =  FirebaseDatabase.getInstance().getReference().child("Trajet Date");
    }

    private void createLayoutTrajet(ArrayList infoTrajet) {

        LinearLayout principalLayoutVertical =(LinearLayout)findViewById(R.id.linLayoutResultat);
        principalLayoutVertical.setGravity(Gravity.CENTER);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout layoutVertical = new LinearLayout(this);
        layoutVertical.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        layoutVertical.setOrientation(LinearLayout.VERTICAL);
        layoutVertical.setGravity(Gravity.CENTER);

        scrollView.addView(layoutVertical);



            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke(3, Color.BLACK);
            drawable.setCornerRadius(2);
            drawable.setColor(Color.LTGRAY);

            LinearLayout layoutHorizontal = new LinearLayout(this);
            layoutHorizontal.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            layoutVertical.setOrientation(LinearLayout.HORIZONTAL);
            layoutHorizontal.setBackgroundDrawable(drawable);
            layoutHorizontal.setGravity(Gravity.CENTER);

            Button b = new Button(this);

            b.setText("Réserver une place");
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


            TextView tv2 = new TextView(this);
            tv2.setText("Lieu de départ : " + infoTrajet.get(2));
            TextView tv3 = new TextView(this);
            tv3.setText("Lieu de d'arrivée : " + infoTrajet.get(1));
            TextView tv4 = new TextView(this);
            tv4.setText("Date : " + strDate);
            TextView tv5 = new TextView(this);
            tv5.setText("Heure : " + infoTrajet.get(0));
            layoutHorizontal.addView(tv2);
            layoutHorizontal.addView(tv3);
            layoutHorizontal.addView(tv4);
            layoutHorizontal.addView(b);
            layoutVertical.addView(layoutHorizontal);
        principalLayoutVertical.addView(scrollView);
    }
}
