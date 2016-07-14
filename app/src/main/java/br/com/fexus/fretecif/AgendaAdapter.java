package br.com.fexus.fretecif;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<Information> data = Collections.emptyList();

    public AgendaAdapter(Context context, List<Information> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.frete_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Information current = data.get(position);
        holder.empresa.setText(current.empresa);
        holder.notaFiscal.setText(current.notaFiscal);
        holder.peso.setText(current.peso);
        holder.valor.setText(current.valor);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView empresa, notaFiscal, peso, valor;

        public MyViewHolder(View itemView) {
            super(itemView);
            empresa = (TextView) itemView.findViewById(R.id.empresa);
            notaFiscal = (TextView) itemView.findViewById(R.id.notaFiscal);
            peso = (TextView) itemView.findViewById(R.id.peso);
            valor = (TextView) itemView.findViewById(R.id.valor);
        }

    }
}
