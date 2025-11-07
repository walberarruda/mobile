package com.example.technicalassistance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminPanelActivity extends AppCompatActivity {
    Button btnCadastroDispositivo, btnGerenciarUsuarios,   btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);


        btnCadastroDispositivo = findViewById(R.id.btnCadastroDispositivo);
        btnGerenciarUsuarios = findViewById(R.id.btnGerenciarUsuarios);
        btnLogout = findViewById(R.id.btnLogout);


        btnCadastroDispositivo.setOnClickListener(v -> startActivity(new Intent(AdminPanelActivity.this, CadastroDispositivoActivity.class)));
        btnGerenciarUsuarios.setOnClickListener(v -> startActivity(new Intent(this, UsuarioManagerActivity.class)));

        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(AdminPanelActivity.this, MainActivity.class));
            finish();
        });
    }
}
