package com.example.technicalassistance;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "assistencia.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABELA_USUARIO = "usuarios";
    private static final String TABELA_DISPOSITIVO = "dispositivos";
    private static final String TABELA_SERVICO = "servicos";

    private static final String SQL_TABELA_USUARIO =
            "CREATE TABLE usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT, " +
                    "usuario TEXT UNIQUE, " +
                    "senha TEXT, " +
                    "tipo TEXT)";

    private static final String SQL_TABELA_DISPOSITIVO =
            "CREATE TABLE dispositivos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tipo TEXT, " +
                    "modelo TEXT, " +
                    "proprietario TEXT, " +
                    "data_entrega TEXT, " +
                    "status TEXT)";

    private static final String SQL_TABELA_SERVICO =
            "CREATE TABLE servicos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "dispositivo_id INTEGER, " +
                    "tipo_servico TEXT, " +
                    "valor REAL, " +
                    "prazo TEXT, " +
                    "status TEXT, " +
                    "FOREIGN KEY(dispositivo_id) REFERENCES dispositivos(id))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_TABELA_USUARIO);
        db.execSQL(SQL_TABELA_DISPOSITIVO);
        db.execSQL(SQL_TABELA_SERVICO);

        // Inserção de usuário padrão admin (pode ser removido se necessário)
        db.execSQL("INSERT INTO usuarios (nome, usuario, senha, tipo) VALUES ('Admin', 'admin', 'admin', 'Administrador')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS dispositivos");
        db.execSQL("DROP TABLE IF EXISTS servicos");
        onCreate(db);
    }

    // Login
    public boolean login(String usuario, String senha) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM usuarios WHERE usuario=? AND senha=?", new String[]{usuario, senha});
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }

    // Buscar tipo de usuário
    public String buscarTipoUsuario(String usuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT tipo FROM usuarios WHERE usuario=?", new String[]{usuario});
        String tipo = "";
        if (cursor.moveToFirst()) {
            tipo = cursor.getString(0);
        }
        cursor.close();
        return tipo;
    }

    // Inserir usuário
    public boolean inserirUsuario(String nome, String usuario, String senha, String tipo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        values.put("usuario", usuario);
        values.put("senha", senha);
        values.put("tipo", tipo);
        try {
            long res = db.insertOrThrow(TABELA_USUARIO, null, values);
            return res != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Verificar se usuário existe
    public boolean checkUsuarioExiste(String usuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM usuarios WHERE usuario=?", new String[]{usuario});
        boolean existe = cursor.moveToFirst();
        cursor.close();
        return existe;
    }

    // Inserir dispositivo
    public boolean inserirDispositivo(String tipo, String modelo, String proprietario, String dataEntrega, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tipo", tipo);
        values.put("modelo", modelo);
        values.put("proprietario", proprietario);
        values.put("data_entrega", dataEntrega);
        values.put("status", status);
        long res = db.insert(TABELA_DISPOSITIVO, null, values);
        return res != -1;
    }

    // Buscar dispositivos do cliente
    public List<String> getDispositivosDoCliente(String usuario) {
        List<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT tipo, modelo, data_entrega, status FROM dispositivos WHERE proprietario=?",
                new String[]{usuario});
        if (cursor.moveToFirst()) {
            do {
                String tipo = cursor.getString(0);
                String modelo = cursor.getString(1);
                String dataEntrega = cursor.getString(2);
                String status = cursor.getString(3);
                String linha = tipo + " - " + modelo +
                        "\nEntrega: " + (dataEntrega != null ? dataEntrega : "Pendente") +
                        "\nStatus: " + (status != null ? status : "Não definido");
                lista.add(linha);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    // Buscar status do serviço pelo id do dispositivo
    public Cursor buscarStatusServico(int dispositivoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT tipo_servico, valor, prazo, status FROM servicos WHERE dispositivo_id=?", new String[]{String.valueOf(dispositivoId)});
    }

    // Buscar todos os usuários/clientes
    public List<Usuario> getTodosUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, nome, usuario, tipo FROM usuarios", null); // Não retornar senha na listagem

        if (cursor.moveToFirst()) {
            do {
                Usuario user = new Usuario(
                        cursor.getInt(0),                    // id
                        cursor.getString(1),                 // nome
                        cursor.getString(2),                 // usuario
                        cursor.getString(3)                  // tipo
                );
                lista.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    // Editar usuário (nome e tipo)
    public boolean editarUsuario(int id, String nome, String tipo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        values.put("tipo", tipo);
        int res = db.update(TABELA_USUARIO, values, "id=?", new String[]{String.valueOf(id)});
        return res > 0;
    }

    // Deletar usuário por ID
    public boolean deletarUsuario(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete(TABELA_USUARIO, "id=?", new String[]{String.valueOf(id)});
        return res > 0;
    }

    // Buscar dispositivos do usuário por nome
    public List<String> getDispositivosPorUsuario(String usuario){
        List<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT tipo || ' - ' || modelo FROM dispositivos WHERE proprietario=?", new String[]{usuario});
        if (cursor.moveToFirst()) {
            do {
                lista.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    // Atualizar senha de usuário
    public boolean atualizarSenhaUsuario(int id, String novaSenha){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("senha", novaSenha);
        int res = db.update(TABELA_USUARIO, values, "id=?", new String[]{String.valueOf(id)});
        return res > 0;
    }

    // Retorna a lista de usuários do tipo Cliente para preencher Spinner de proprietários
    public List<String> getClientes() {
        List<String> clientes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT usuario FROM usuarios WHERE tipo = 'Cliente'", null);
        if (cursor.moveToFirst()) {
            do {
                clientes.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return clientes;
    }

    // Atualizar status e data_entrega
    public boolean atualizarStatusEntrega(int id, String status, String dataEntrega) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        values.put("data_entrega", dataEntrega);
        int res = db.update(TABELA_DISPOSITIVO, values, "id=?", new String[]{String.valueOf(id)});
        return res > 0;
    }

    public boolean atualizarStatusDispositivo(String proprietario, String modelo, String novoStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", novoStatus);
        int res = db.update(TABELA_DISPOSITIVO, values, "proprietario=? AND modelo=?", new String[]{proprietario, modelo});
        return res > 0;
    }

    public List<Dispositivo> getDispositivosDetalhados() {
        List<Dispositivo> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, tipo, modelo, proprietario, data_entrega, status FROM dispositivos", null);
        if (cursor.moveToFirst()) {
            do {
                lista.add(new Dispositivo(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public boolean atualizarDispositivo(int id, String tipo, String modelo, String proprietario, String dataEntrega, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tipo", tipo);
        values.put("modelo", modelo);
        values.put("proprietario", proprietario);
        values.put("data_entrega", dataEntrega);
        values.put("status", status);
        int res = db.update("dispositivos", values, "id=?", new String[]{String.valueOf(id)});
        return res > 0;
    }

}
