package com.cartoponto.model;

import com.cartoponto.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registro {
    private String idRegistro;
    private String data;
    private String imgUrl;

    public Registro(/*String idRegistro, String data, String imgUrl*/){
        DatabaseReference ref = ConfiguracaoFirebase.getFirebase().child("meus_registros");
        setIdRegistro(ref.push().getKey());
    }

    public void salvar(){
        String idRegistro = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_registros");

        anuncioRef.child(idRegistro)
                .child(getIdRegistro())
                .setValue(this);
    }
    public String getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(String idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
