package com.example.iwimapplication.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iwimapplication.Modal.User;
import com.example.iwimapplication.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class listStudent extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ManageStudents";

    ImageView back;
    ListView list;
    String statut;
    static ArrayList<User> myList;

    FirebaseFirestore firestore;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);
        back = findViewById(R.id.ab_back);

        back.setOnClickListener(this);

        list = findViewById(R.id.usersList);
        list.setDivider(null);
        list.setDividerHeight(20);

        statut = "Etudiant";

        myList = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();

        firestore.collection("users").whereIn("statut", Collections.singletonList(statut)).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.d("Snapshot","Error: "+e.getMessage());
                }
                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED) {
                        User user = new User();
                        user.setUid(doc.getDocument().getId());
                        user.setNom(doc.getDocument().getString("nom"));
                        user.setPrenom(doc.getDocument().getString("prenom"));
                        user.setEmail(doc.getDocument().getString("email"));
                        System.out.println(user);
                        myList.add(user);
                    }
                }

                listStudent.myAdapter adapter = new listStudent.myAdapter(getApplicationContext(), myList);
                list.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ab_back){
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        }
    }

    class myAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<User> usersList = null;

        myAdapter(Context context, ArrayList<User> list){
            super(context, R.layout.useritem);
            this.context=context;
            this.usersList=list;

            if(this.usersList.size()==0){
                Toast.makeText(getApplicationContext(),"<liste vide>",
                        Toast.LENGTH_SHORT).show();
            }
        }

        public int getCount() {
            return usersList.size();
        }


        public View getView (int position,@Nullable View converView, @Nullable ViewGroup parent){
            LayoutInflater layoutInflater= (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View row = layoutInflater.inflate(R.layout.studentitem, parent, false);

            ImageView photo = row.findViewById(R.id.userPicture);
            final TextView nom = row.findViewById(R.id.userName);
            final TextView email = row.findViewById(R.id.userEmail);
            ImageView contact = row.findViewById(R.id.userPhone);


            User user = this.usersList.get(position);

            final String uid = user.getUid();

            photo.setImageResource(R.drawable.profile);

            nom.setText(user.getNom()+" "+user.getPrenom());
            email.setText(user.getEmail());

            contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){

                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));

                    intent.putExtra(Intent.EXTRA_EMAIL,new String[]{email.getText().toString()});
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }

                }
            });

            return row;
        }
    }
}
