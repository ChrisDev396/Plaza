package com.cartoponto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cartoponto.model.Registro;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView listView;
    private List<Registro> registros = new ArrayList<>();
    private RegistroAdapter registroAdapter;
    private DatabaseReference registroUsuarioRef;

    private FirebaseAuth auth;
    private Button btnRegistrar;
    private Button btnCadastrar;
    private EditText editCnpj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        obterUI();
        registroUsuarioRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_registros")
                .child( ConfiguracaoFirebase.getIdUsuario() );

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistroActivity.class));
            }
        });

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                registro.setCnpj(editCnpj.getText().toString());
//                registro.salvarInfosCadastro();
            }
        });

        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setHasFixedSize(true);
        registroAdapter = new RegistroAdapter(registros, this);
        listView.setAdapter( registroAdapter );

        recuperarRegistros();

    }

    private void recuperarRegistros() {
        registroUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                registros.clear();
                for ( DataSnapshot ds : dataSnapshot.getChildren() ){
                    registros.add( ds.getValue(Registro.class) );
                }

                Collections.reverse( registros );
                registroAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void obterUI(){
        editCnpj = findViewById(R.id.editTextText);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        listView = findViewById(R.id.listView);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_sair){
            auth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}