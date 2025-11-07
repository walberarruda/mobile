package com.example.technicalassistance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.graphics.Color;

import java.util.List;

public class DispositivoAdapter extends ArrayAdapter<Dispositivo> {
    private Context context;
    private List<Dispositivo> dispositivos;

    public DispositivoAdapter(Context context, List<Dispositivo> dispositivos) {
        super(context, 0, dispositivos);
        this.context = context;
        this.dispositivos = dispositivos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Dispositivo dispositivo = dispositivos.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_dispositivo, parent, false);
        }

        TextView txtTipoModelo = convertView.findViewById(R.id.txtTipoModelo);
        TextView txtProprietario = convertView.findViewById(R.id.txtProprietario);
        TextView txtDataEntrega = convertView.findViewById(R.id.txtDataEntrega);
        View statusIndicator = convertView.findViewById(R.id.statusIndicator);

        txtTipoModelo.setText(dispositivo.tipo + " - " + dispositivo.modelo);
        txtProprietario.setText("Proprietário: " + dispositivo.proprietario);
        txtDataEntrega.setText("Entrega: " + dispositivo.dataEntrega);

        // Definir cor do indicador conforme status
        switch (dispositivo.status.toLowerCase()) {
            case "aguardando":
                statusIndicator.setBackgroundColor(Color.YELLOW);
                break;
            case "aprovado":
                statusIndicator.setBackgroundColor(Color.GREEN);
                break;
            case "desaprovado":
                statusIndicator.setBackgroundColor(Color.RED);
                break;
            default:
                statusIndicator.setBackgroundColor(Color.GRAY);
                break;
        }

        return convertView;
    }
}
