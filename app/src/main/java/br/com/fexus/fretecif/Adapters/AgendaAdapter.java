package br.com.fexus.fretecif.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.fexus.fretecif.Extra.Information;
import br.com.fexus.fretecif.R;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    public static ArrayList<Information> dataInfo = new ArrayList<>();

    public AgendaAdapter(Context context, ArrayList<Information> dataInfo) {
        inflater = LayoutInflater.from(context);
        AgendaAdapter.dataInfo = dataInfo;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.frete_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Information current = dataInfo.get(position);
        holder.empresa.setText("Empresa: ".concat(current.getEmpresa()));
        holder.empresaDestiny.setText("Destino: ".concat((current.getEmpresaDestiny())));
        holder.notaFiscal.setText("NF ".concat(current.getNotaFiscal()));
        if (!current.getPeso().trim().equals("")) {
            holder.peso.setText("Peso: ".concat(current.getPeso().concat("Kg")));
        } else {
            holder.peso.setText("Peso: ");
        }
        holder.valor.setText("Valor: ".concat(current.getValor()));
    }

    @Override
    public int getItemCount() {
        return dataInfo.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView empresa, empresaDestiny, notaFiscal, peso, valor;

        public MyViewHolder(View itemView) {
            super(itemView);
            empresa = (TextView) itemView.findViewById(R.id.empresa);
            empresaDestiny = (TextView) itemView.findViewById(R.id.empresaDestiny);
            notaFiscal = (TextView) itemView.findViewById(R.id.notaFiscal);
            peso = (TextView) itemView.findViewById(R.id.peso);
            valor = (TextView) itemView.findViewById(R.id.valor);
        }

    }

}
