package com.example.technicalassistance;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.technicalassistance.DatabaseHelper;


import com.example.technicalassistance.R;

public class CadastroUsuarioActivity extends AppCompatActivity {
    private EditText editTextNome, editTextUsuario, editTextSenha;
    private Spinner spinnerTipo;
    private Button buttonSalvarUsuario;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        editTextNome = findViewById(R.id.editTextNome);
        editTextUsuario = findViewById(R.id.editTextUsuario);
        editTextSenha = findViewById(R.id.editTextSenha);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        buttonSalvarUsuario = findViewById(R.id.buttonSalvarUsuario);
        db = new DatabaseHelper(this);

        buttonSalvarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = editTextNome.getText().toString().trim();
                String usuario = editTextUsuario.getText().toString().trim();
                String senha = editTextSenha.getText().toString().trim();
                String tipo = spinnerTipo.getSelectedItem().toString();

                if (usuario.isEmpty() || senha.isEmpty() || nome.isEmpty()) {
                    Toast.makeText(CadastroUsuarioActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (db.checkUsuarioExiste(usuario)) {
                    Toast.makeText(CadastroUsuarioActivity.this, "Usuário já cadastrado!", Toast.LENGTH_SHORT).show();
                } else {
                    if(db.inserirUsuario(nome, usuario, senha, tipo)){
                        Toast.makeText(CadastroUsuarioActivity.this, "Usuário cadastrado!", Toast.LENGTH_SHORT).show();
                        finish(); // fecha a tela e volta para a anterior (login)
                    } else {
                        Toast.makeText(CadastroUsuarioActivity.this, "Erro ao cadastrar!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
