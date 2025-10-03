package com.example.visualizandoimagens;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.*;
import android.view.*;

public class MainActivity extends AppCompatActivity {
    ImageSwitcher imgFoto, imgSobre;
    Button btanterior, btproximo;

    int indice = 1;

    public void mostrarInfoPersonagem(){
        switch (indice){
            case 1:{
                imgFoto.setImageResource(R.drawable.foto_deadpool);
                imgSobre.setImageResource(R.drawable.frase_sobre_deadpool);
            }break;

            case 2:{
                imgFoto.setImageResource(R.drawable.foto_colossus);
                imgSobre.setImageResource(R.drawable.frase_sobre_colossus);
            }break;

            case 3:{
                imgFoto.setImageResource(R.drawable.foto_megasonico);
                imgSobre.setImageResource(R.drawable.frase_sobre_megasonico);
            }break;

            case 4:{
                imgFoto.setImageResource(R.drawable.deadpoolwolverine);
                imgSobre.setImageResource(R.drawable.txtdeadpoolwolverine);
            }break;
        }

    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btanterior = (Button) findViewById(R.id.btanterior);
        btproximo = (Button) findViewById(R.id.btproximo);

        imgFoto = (ImageSwitcher) findViewById(R.id.imgFoto);

        imgFoto.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_XY);
                myView.setLayoutParams( new
                    ImageSwitcher.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.WRAP_CONTENT));

                return myView;
            }
        });

        imgSobre = (ImageSwitcher) findViewById(R.id.imgSobre);

        imgSobre.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_XY);
                myView.setLayoutParams( new
                        ImageSwitcher.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.WRAP_CONTENT));

                return myView;
            }
        });


        //carrega a foto do deadpool
        imgFoto.setImageResource(R.drawable.foto_deadpool);
        //carrega a info sobre deadpool
        imgSobre.setImageResource(R.drawable.frase_sobre_deadpool);

        btanterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(indice > 1){
                    indice --;
                    mostrarInfoPersonagem();
                }
            }
        });

        btproximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(indice < 4){
                    indice ++;
                    mostrarInfoPersonagem();
                }
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.imgFoto), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}