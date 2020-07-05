package com.example.bcard;


import android.os.Bundle;

import android.widget.ListView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class bcardlist extends AppCompatActivity {
    ArrayList<Contact> data;
    private ListView mlistview;
    private contactAdapter adapter;


    private DatabaseReference mDatabaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bcardlist);
        mlistview =(ListView)findViewById(R.id.list_item);


        data= new ArrayList<Contact>();

        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Contact contact = postSnapshot.getValue(Contact.class);
                    data.add(contact);
                }
                adapter =new contactAdapter(bcardlist.this,R.layout.model,data);
                mlistview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(bcardlist.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
