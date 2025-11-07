package com.example.technicalassistance;

import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.app.DatePickerDialog;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

public class CadastroDispositivoActivity extends AppCompatActivity {

    private EditText editTextTipoDispositivo, editTextModelo, editTextDataEntrega;
    private Spinner spinnerProprietario, spinnerStatus;
    private Button buttonPickDate, buttonSalvarDispositivo;
    private ListView listViewDispositivos;
    private DatabaseHelper db;
    private String dataSelecionada = "";

    private List<Dispositivo> dispositivosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_dispositivo);

        editTextTipoDispositivo = findViewById(R.id.editTextTipoDispositivo);
        editTextModelo = findViewById(R.id.editTextModelo);
        editTextDataEntrega = findViewById(R.id.editTextDataEntrega);
        spinnerProprietario = findViewById(R.id.spinnerProprietario);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        buttonPickDate = findViewById(R.id.buttonPickDate);
        buttonSalvarDispositivo = findViewById(R.id.buttonSalvarDispositivo);
        listViewDispositivos = findViewById(R.id.listViewDispositivos);

        db = new DatabaseHelper(this);

        carregarProprietarios();
        carregarStatus();
        carregarDispositivos();

        buttonPickDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, y, m, d) -> {
                        dataSelecionada = String.format("%02d/%02d/%04d", d, m + 1, y);
                        editTextDataEntrega.setText(dataSelecionada);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        buttonSalvarDispositivo.setOnClickListener(v -> {
            String tipo = editTextTipoDispositivo.getText().toString().trim();
            String modelo = editTextModelo.getText().toString().trim();
            String proprietario = spinnerProprietario.getSelectedItem() != null ? spinnerProprietario.getSelectedItem().toString() : "";
            String dataEntrega = editTextDataEntrega.getText().toString().trim();
            String status = spinnerStatus.getSelectedItem() != null ? spinnerStatus.getSelectedItem().toString() : "";

            if (tipo.isEmpty() || modelo.isEmpty() || proprietario.isEmpty() || dataEntrega.isEmpty() || status.isEmpty()) {
                Toast.makeText(CadastroDispositivoActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.inserirDispositivo(tipo, modelo, proprietario, dataEntrega, status)) {
                Toast.makeText(CadastroDispositivoActivity.this, "Dispositivo cadastrado!", Toast.LENGTH_SHORT).show();
                limparCampos();
                carregarDispositivos();
            } else {
                Toast.makeText(CadastroDispositivoActivity.this, "Erro ao cadastrar!", Toast.LENGTH_SHORT).show();
            }
        });

        listViewDispositivos.setOnItemClickListener((parent, view, position, id) -> {
            Dispositivo dispositivoSelecionado = dispositivosList.get(position);
            abrirDialogEdicaoDispositivo(dispositivoSelecionado);
        });
    }

    private void carregarProprietarios() {
        List<String> clientes = db.getClientes();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, clientes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProprietario.setAdapter(adapter);
    }

    private void carregarStatus() {
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Aguardando", "Aprovado", "Desaprovado"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
    }

    private void carregarDispositivos() {
        dispositivosList = db.getDispositivosDetalhados();
        DispositivoAdapter adapter = new DispositivoAdapter(this, dispositivosList);
        listViewDispositivos.setAdapter(adapter);
    }

    private void limparCampos() {
        editTextTipoDispositivo.setText("");
        editTextModelo.setText("");
        editTextDataEntrega.setText("");
        dataSelecionada = "";
        if (spinnerProprietario.getAdapter() != null) {
            spinnerProprietario.setSelection(0);
        }
        if (spinnerStatus.getAdapter() != null) {
            spinnerStatus.setSelection(0);
        }
    }

    private void abrirDialogEdicaoDispositivo(Dispositivo dispositivo) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_editar_dispositivo, null);

        EditText editTipo = dialogView.findViewById(R.id.editDialogTipo);
        EditText editModelo = dialogView.findViewById(R.id.editDialogModelo);
        Spinner spinnerProprietarioDialog = dialogView.findViewById(R.id.spinnerDialogProprietario);
        EditText editDataEntrega = dialogView.findViewById(R.id.editDialogDataEntrega);
        Spinner spinnerStatusDialog = dialogView.findViewById(R.id.spinnerDialogStatus);

        editTipo.setText(dispositivo.tipo);
        editModelo.setText(dispositivo.modelo);
        editDataEntrega.setText(dispositivo.dataEntrega);

        // Carregar proprietarios no spinner dialog
        List<String> clientes = db.getClientes();
        ArrayAdapter<String> adapterClientes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, clientes);
        adapterClientes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProprietarioDialog.setAdapter(adapterClientes);
        int posCliente = clientes.indexOf(dispositivo.proprietario);
        if (posCliente >= 0) spinnerProprietarioDialog.setSelection(posCliente);

        // Carregar status no spinner dialog
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Aguardando", "Aprovado", "Desaprovado"});
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatusDialog.setAdapter(adapterStatus);
        int posStatus = adapterStatus.getPosition(dispositivo.status);
        if (posStatus >=0) spinnerStatusDialog.setSelection(posStatus);

        new AlertDialog.Builder(this)
                .setTitle("Editar Dispositivo")
                .setView(dialogView)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String novoTipo = editTipo.getText().toString().trim();
                    String novoModelo = editModelo.getText().toString().trim();
                    String novoProprietario = spinnerProprietarioDialog.getSelectedItem().toString();
                    String novaDataEntrega = editDataEntrega.getText().toString().trim();
                    String novoStatus = spinnerStatusDialog.getSelectedItem().toString();

                    if(novoTipo.isEmpty() || novoModelo.isEmpty() || novaDataEntrega.isEmpty()) {
                        Toast.makeText(this, "Complete todos os campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean atualizado = db.atualizarDispositivo(dispositivo.id, novoTipo, novoModelo, novoProprietario, novaDataEntrega, novoStatus);
                    if(atualizado) {
                        Toast.makeText(this, "Dispositivo atualizado", Toast.LENGTH_SHORT).show();
                        carregarDispositivos();
                    } else {
                        Toast.makeText(this, "Erro ao atualizar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
