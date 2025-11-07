package com.example.technicalassistance;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.database.Cursor;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.technicalassistance.DatabaseHelper;

import com.example.technicalassistance.R;

public class ConsultaClienteActivity extends AppCompatActivity {
    private EditText editTextCodigoUnico, editTextSenhaCliente;
    private Button buttonConsultar;
    private TextView textViewStatus;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_cliente);

        editTextCodigoUnico = findViewById(R.id.editTextCodigoUnico);
        editTextSenhaCliente = findViewById(R.id.editTextSenhaCliente);
        buttonConsultar = findViewById(R.id.buttonConsultar);
        textViewStatus = findViewById(R.id.textViewStatus);
        db = new DatabaseHelper(this);

        buttonConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int codDispositivo = Integer.parseInt(editTextCodigoUnico.getText().toString());
                Cursor cursor = db.buscarStatusServico(codDispositivo);
                if (cursor.moveToFirst()) {
                    String status = cursor.getString(cursor.getColumnIndex("status"));
                    textViewStatus.setText("Status: " + status);
                    // Pode mostrar mais detalhes (tipo serviço, prazo, valor)
                } else {
                    textViewStatus.setText("Código não localizado.");
                }
                cursor.close();
            }
        });
    }
}
