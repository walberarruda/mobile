package com.example.technicalassistance;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.example.technicalassistance.DatabaseHelper;

import com.example.technicalassistance.R;


public class MainActivity extends AppCompatActivity {
    EditText editTextUsuario, editTextSenha;
    Button buttonEntrar, buttonCadastro;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsuario = findViewById(R.id.editTextUsuario);
        editTextSenha = findViewById(R.id.editTextSenha);
        buttonEntrar = findViewById(R.id.buttonEntrar);
//        buttonCadastro = findViewById(R.id.buttonCadastro);
        db = new DatabaseHelper(this);

        buttonEntrar.setOnClickListener(v -> {
            String usuario = editTextUsuario.getText().toString().trim();
            String senha = editTextSenha.getText().toString().trim();

            if (db.login(usuario, senha)) {
                String tipo = db.buscarTipoUsuario(usuario);

                if (tipo.equalsIgnoreCase("Cliente")) {
                    Intent intent = new Intent(MainActivity.this, ClientePanelActivity.class);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);

                } else if (tipo.equalsIgnoreCase("Administrador") || tipo.equalsIgnoreCase("Funcionario")) {
                    Intent intent = new Intent(MainActivity.this, AdminPanelActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Tipo de usuário desconhecido", Toast.LENGTH_SHORT).show();
                    return;
                }

                finish(); // apenas fecha login após abrir o painel correto
            } else {
                Toast.makeText(MainActivity.this, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show();
            }
        });


//        buttonCadastro.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CadastroUsuarioActivity.class)));
    }
}
