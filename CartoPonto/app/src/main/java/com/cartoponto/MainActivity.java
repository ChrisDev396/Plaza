package com.cartoponto;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth auth;
    private Button btnGoogle;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("136731751193-m6sloe55v9893bgtvuglkf2et8lck40o.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // O usuário está logado, você pode realizar ações apropriadas aqui
            // Por exemplo, redirecioná-lo para uma tela de perfil ou apresentar conteúdo personalizado.
            String userEmail = currentUser.getEmail();
            Toast.makeText(this, "Bem-vindo, " + userEmail, Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        } else {
            // O usuário não está logado, você pode redirecioná-lo para a tela de login ou registro.
            // Por exemplo:
            // Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            // startActivity(loginIntent);
        }

        btnGoogle = findViewById(R.id.btnLoginGoogle);

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });
    }
    private void googleSignIn(){
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // Autenticação com o Google bem-sucedida
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String idToken = account.getIdToken();

                // Autenticar no Firebase Authentication com o token do Google
                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(this, task2 -> {
                            if (task2.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                finish();
                                startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                            } else {
                                // Tratar falha na autenticação no Firebase
                            }
                        });
            } catch (ApiException e) {
                // Tratar erro na autenticação com o Google
            }
        }
    }

}