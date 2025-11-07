package com.example.technicalassistance;

import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;
import android.widget.*;
import android.view.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class UsuarioManagerActivity extends AppCompatActivity {
    ListView listViewUsuarios;
    Button btnAdicionar;
    DatabaseHelper db;
    ArrayAdapter<Usuario> adapter;
    List<Usuario> usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_manager);

        listViewUsuarios = findViewById(R.id.listViewUsuarios);
        btnAdicionar = findViewById(R.id.btnAdicionar);
        db = new DatabaseHelper(this);

        carregarUsuarios();

        btnAdicionar.setOnClickListener(v -> abrirDialogCadastro());

        listViewUsuarios.setOnItemClickListener((parent, view, position, id) -> {
            Usuario selected = usuarios.get(position);
            abrirDialogEdicao(selected);
        });

        listViewUsuarios.setOnItemLongClickListener((parent, view, position, id) -> {
            Usuario usuarioSelecionado = usuarios.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Opções")
                    .setItems(new CharSequence[]{"Editar", "Deletar", "Ver Dispositivos"}, (dialog, which) -> {
                        switch (which) {
                            case 0:
                                abrirDialogEdicao(usuarioSelecionado);
                                break;
                            case 1:
                                confirmarDelecao(usuarioSelecionado);
                                break;
                            case 2:
                                mostrarDispositivos(usuarioSelecionado);
                                break;
                        }
                    }).show();
            return true;
        });
    }


    private void carregarUsuarios() {
        usuarios = db.getTodosUsuarios();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usuarios);
        listViewUsuarios.setAdapter(adapter);
    }

    private void abrirDialogCadastro() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_usuario, null);
        EditText nome = dialogView.findViewById(R.id.editDialogNome);
        EditText usuario = dialogView.findViewById(R.id.editDialogUsuario);
        EditText senha = dialogView.findViewById(R.id.editDialogSenha);
        Spinner tipo = dialogView.findViewById(R.id.spinnerDialogTipo);

        new AlertDialog.Builder(this)
                .setTitle("Novo Usuário")
                .setView(dialogView)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    if(nome.getText().toString().isEmpty() || usuario.getText().toString().isEmpty() || senha.getText().toString().isEmpty()) {
                        Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(db.checkUsuarioExiste(usuario.getText().toString())) {
                        Toast.makeText(this, "Usuário já existe!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.inserirUsuario(nome.getText().toString(), usuario.getText().toString(), senha.getText().toString(), tipo.getSelectedItem().toString());
                    carregarUsuarios();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void abrirDialogEdicao(Usuario usuario) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_usuario_edicao, null);
        EditText nome = dialogView.findViewById(R.id.editDialogNomeEdicao);
        Spinner tipo = dialogView.findViewById(R.id.spinnerDialogTipoEdicao);
        EditText senha = dialogView.findViewById(R.id.editDialogSenhaEdicao); // novo campo senha na edição

        nome.setText(usuario.nome);
        // configurar spinner para o valor atual do tipo do usuário (não implementado aqui)

        new AlertDialog.Builder(this)
                .setTitle("Editar Usuário")
                .setView(dialogView)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    db.editarUsuario(usuario.id, nome.getText().toString(), tipo.getSelectedItem().toString());
                    String novaSenha = senha.getText().toString();
                    if(!novaSenha.isEmpty()) {
                        db.atualizarSenhaUsuario(usuario.id, novaSenha);
                    }
                    carregarUsuarios();
                    Toast.makeText(this, "Usuário atualizado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarDelecao(Usuario usuario){
        new AlertDialog.Builder(this)
                .setTitle("Confirmar deleção")
                .setMessage("Deseja deletar o usuário " + usuario.nome + "?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    if(db.deletarUsuario(usuario.id)){
                        carregarUsuarios();
                        Toast.makeText(this, "Usuário deletado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Erro ao deletar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void mostrarDispositivos(Usuario usuario) {
        List<String> dispositivos = db.getDispositivosPorUsuario(usuario.usuario);
        String mensagem = dispositivos.isEmpty() ? "Sem dispositivos cadastrados." : "";

        for (String d : dispositivos) {
            mensagem += d + "\n";
        }

        new AlertDialog.Builder(this)
                .setTitle("Dispositivos de " + usuario.nome)
                .setMessage(mensagem)
                .setPositiveButton("Ok", null)
                .show();
    }
}
