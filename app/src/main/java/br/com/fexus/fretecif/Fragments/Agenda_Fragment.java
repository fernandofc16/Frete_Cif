package br.com.fexus.fretecif.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import br.com.fexus.fretecif.Adapters.AgendaAdapter;
import br.com.fexus.fretecif.Extra.Information;
import br.com.fexus.fretecif.R;

public class Agenda_Fragment extends Fragment {

    public Agenda_Fragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_agenda_lista, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        AgendaAdapter adapter = new AgendaAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    public static ArrayList<Information> getData() {
        ArrayList<Information> data = new ArrayList<>();

        String[] empresas = {"Adecol"}, notasFiscais = {"5588253"}, pesos = {"1200"}, valores = {"450"};

        for(int i = 0; i < empresas.length; i++) {
            Information current = new Information();
            current.setEmpresa(empresas[i]);
            current.setNotaFiscal(notasFiscais[i]);
            current.setPeso(pesos[i]);
            current.setValor(valores[i]);
        }

        return data;
    }

}
