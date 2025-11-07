package com.example.technicalassistance;

import android.os.Bundle;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.view.View;
import android.app.AlertDialog;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.HashMap;

public class ClientePanelActivity extends AppCompatActivity {

    ListView listViewDispositivos;
    Button btnSair;
    DatabaseHelper db;
    String usuarioLogado;
    List<String> dispositivos;
    HashMap<String, String> dispositivosStatus; // Map para status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_panel);

        listViewDispositivos = findViewById(R.id.listViewDispositivos);
        btnSair = findViewById(R.id.btnSair);
        db = new DatabaseHelper(this);
        usuarioLogado = getIntent().getStringExtra("usuario");

        carregarDispositivos();

        btnSair.setOnClickListener(v -> {
            startActivity(new Intent(ClientePanelActivity.this, MainActivity.class));
            finish();
        });

        listViewDispositivos.setOnItemClickListener((parent, view, position, id) -> {
            String item = dispositivos.get(position);
            String statusAtual = dispositivosStatus.get(item);
            abrirDialogAlterarStatus(item, statusAtual);
        });
    }

    private void carregarDispositivos() {
        dispositivos = db.getDispositivosDoCliente(usuarioLogado);
        dispositivosStatus = new HashMap<>();
        for (String dispositivoInfo : dispositivos) {
            String[] partes = dispositivoInfo.split("\nStatus: ");
            if (partes.length > 1) {
                dispositivosStatus.put(dispositivoInfo, partes[1]);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dispositivos) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                String item = dispositivos.get(position);
                String status = dispositivosStatus.get(item);
                if (status != null) {
                    int cor = Color.YELLOW; // padrão aguardando
                    if (status.equalsIgnoreCase("Aprovado")) cor = Color.GREEN;
                    else if (status.equalsIgnoreCase("Desaprovado")) cor = Color.RED;
                    v.setBackgroundColor(cor);
                }
                return v;
            }
        };
        listViewDispositivos.setAdapter(adapter);
    }

    private void abrirDialogAlterarStatus(String dispositivo, String statusAtual) {
        String[] opcoes = {"Aguardando", "Aprovado", "Desaprovado"};
        int selecionado = 0;
        for(int i=0; i<opcoes.length; i++) {
            if (opcoes[i].equalsIgnoreCase(statusAtual)) {
                selecionado = i;
                break;
            }
        }
        new AlertDialog.Builder(this)
                .setTitle("Alterar Status")
                .setSingleChoiceItems(opcoes, selecionado, null)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                    String novoStatus = opcoes[selectedPosition];
                    // extrair modelo do dispositivo para identificar no DB, dependendo do formato do item
                    String modelo = extrairModeloDoItem(dispositivo);
                    if(db.atualizarStatusDispositivo(usuarioLogado, modelo, novoStatus)) {
                        carregarDispositivos();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String extrairModeloDoItem(String item) {
        String[] linhas = item.split("\n");
        if (linhas.length > 0 && linhas[0].contains("-")) {
            return linhas[0].split("-")[1].trim();
        }
        return "";
    }
}
