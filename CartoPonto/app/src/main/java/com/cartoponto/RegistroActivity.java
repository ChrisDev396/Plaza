package com.cartoponto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cartoponto.model.Registro;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistroActivity extends AppCompatActivity {


    private ImageView imgFoto;
    private TextView txtTipo;
    private TextView txtData;
    private Button btnFoto;
    private Button btnSalvar;

    Registro registro;
    StorageReference storage;

    Uri imagemUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        obterUI();
        preencherUI();
        registro = configurarRegistro();
        storage = ConfiguracaoFirebase.getFirebaseStorage();

        if( storage != null ){
            Log.i("TAG2", "teste");
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // A permissão da câmera ainda não foi concedida. Solicite-a ao usuário.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2);
        } else {
            // A permissão da câmera já foi concedida. Você pode acessar a câmera.
            //abrirCamera();
        }

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tirarFoto();
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StorageReference imagemRef = storage.child("imagens")
                        .child("registros").child(registro.getIdRegistro());

                UploadTask uploadTask = imagemRef.putFile(imagemUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                registro.setImgUrl( task.getResult().toString());
                                registro.salvar();
                                finish();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("TAG2", "Falha ao fazer upload: " + e.getMessage());
                    }
                });
            }
        });

    }


    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, registro.getIdRegistro(), null);
        return Uri.parse(path);
    }

    private Registro configurarRegistro(){
        Registro registro = new Registro();
        registro.setData(txtData.getText().toString());
        //registro.setImgUrl(txtData.getText().toString());
        return registro;
    }

    private void obterUI(){
        imgFoto = findViewById(R.id.imgFoto);
        txtTipo = findViewById(R.id.txtTipo);
        txtData = findViewById(R.id.txtData);
        btnFoto = findViewById(R.id.btnFoto);
        btnSalvar = findViewById(R.id.btnSalvar);

    }

    private void tirarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, 2);
    }

    private void preencherUI(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime dataHoraAtual = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String dataHoraFormatada = dataHoraAtual.format(formatter);

            txtData.setText(dataHoraFormatada);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap != null) {
                    imgFoto.setImageBitmap(imageBitmap);
                    //Uri myUri = Uri.parse(uriString);
                    imagemUri = getImageUriFromBitmap(imageBitmap);
                    Toast.makeText(this, "Bem-vindo, " + imageBitmap, Toast.LENGTH_SHORT).show();
                        // Agora você tem a URI da imagem na variável "imagemUri"
                } else {
                    // Handle the case where imageBitmap is null
                }
            }
        }
    }
}