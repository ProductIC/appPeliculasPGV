package com.example.peliculasianapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetalleActivity extends AppCompatActivity {

    private static String MOVIE_BASE_URL;
    private TextView titulo;
    private TextView Sinopsis;
    private ImageView img;
    private Button reparto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        MOVIE_BASE_URL="https://image.tmdb.org/t/p/w185";

        titulo = findViewById(R.id.txtTitulo);
        Sinopsis = findViewById(R.id.txtSinopsis);
        img = findViewById(R.id.imgPeli);
        reparto = findViewById(R.id.btnAct);

        Bundle p = getIntent().getExtras();
        setTitle(p.getString("titulo"));
        titulo.setText(p.getString("titulo"));
        Sinopsis.setText(p.getString("sinopsis"));
        Picasso.get().load(MOVIE_BASE_URL + p.getString("imagen")).into(img);

        reparto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DetalleActor.class);
                intent.putExtra("id", p.getString("id"));
                intent.putExtra("titulo", p.getString("titulo"));
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}