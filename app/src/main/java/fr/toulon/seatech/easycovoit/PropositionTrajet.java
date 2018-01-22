package fr.toulon.seatech.easycovoit;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PropositionTrajet extends AppCompatActivity {

    DatabaseReference mRootRef, mTrajetref, mUserID, mTrajetID, mHeureRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposition_trajet);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mTrajetref = mRootRef.child("Trajet");

        mUserID = mTrajetref.child("User ID");

        mTrajetID = mTrajetref.child("Trajet ID");

        mHeureRef =  mTrajetref.child("Heure");

    }
}
