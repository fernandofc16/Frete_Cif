package br.com.fexus.fretecif.Extra;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DateCount {

    public static List<String> diferencaDeDatas(String inicio, String fim){
        // Tranforma Date em String e vice-versa
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        // Vamos zerar os campos que não vamos utilizar!
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        // quebramos a String com as datas atualizar cada campo corespondente
        final String[] valI = inicio.split("/");
        final String[] valF = fim.split("/");
        // Clonamos os Calendar para não ter a mesma referencia
        Calendar cINI = (Calendar) c.clone();
        Calendar cFIM = (Calendar) c.clone();

        // Setamos o valor da dataInfo inicial
        cINI.set(Calendar.DAY_OF_MONTH, Integer.valueOf(valI[0]));
        cINI.set(Calendar.MONTH, Integer.valueOf(valI[1]) - 1);
        cINI.set(Calendar.YEAR, Integer.valueOf(valI[2]));

        // Setamos os valores da dataInfo final
        cFIM.set(Calendar.DAY_OF_MONTH, Integer.valueOf(valF[0]));
        cFIM.set(Calendar.MONTH, Integer.valueOf(valF[1]) - 1);
        cFIM.set(Calendar.YEAR, Integer.valueOf(valF[2]));

        // Se a dataInfo final for menor ou igual a inicial  retorna uma lista vazia...
        if(cFIM.getTimeInMillis() <= cINI.getTimeInMillis()){
            return new ArrayList<>(0);
        }
        // Lista que vamos retonar com o valores
        List<String> itens = new ArrayList<>(0);

        // adicionamos +1 dia, pois não iremos contar o dia inicial
        //cINI.set(Calendar.DAY_OF_MONTH, cINI.get(Calendar.DAY_OF_MONTH)+1);

        // vamos realizar a acão enquanto a dataInfo inicial for menor q a final
        while(cINI.getTimeInMillis() <= cFIM.getTimeInMillis()){
            // adicionamos na lista...
            itens.add(sdf.format(cINI.getTime()));
            // adicionamos +1 dia....
            cINI.set(Calendar.DAY_OF_MONTH, cINI.get(Calendar.DAY_OF_MONTH)+1);
        }

        return itens;
    }

}
