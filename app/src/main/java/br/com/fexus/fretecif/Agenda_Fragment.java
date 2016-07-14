package br.com.fexus.fretecif;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class Agenda_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private AgendaAdapter adapter;

    public Agenda_Fragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_agenda_, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        adapter = new AgendaAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    public static List<Information> getData() {
        List<Information> data = new ArrayList<>();

        String[] empresas = {"Adecol"}, notasFiscais = {"5588253"}, pesos = {"1200"}, valores = {"450"};

        for(int i = 0; i < empresas.length; i++) {
            Information current = new Information();
            current.empresa = empresas[i];
            current.notaFiscal = notasFiscais[i];
            current.peso = pesos[i];
            current.valor = valores[i];
        }

        return data;
    }

}
