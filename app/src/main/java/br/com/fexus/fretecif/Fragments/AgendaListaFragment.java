package br.com.fexus.fretecif.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

import br.com.fexus.fretecif.Adapters.AgendaAdapter;
import br.com.fexus.fretecif.Database.DatabaseHandler;
import br.com.fexus.fretecif.Extra.Information;
import br.com.fexus.fretecif.R;

public class AgendaListaFragment extends Fragment {

    private DatabaseHandler database;
    public static AgendaAdapter adapter;

    public AgendaListaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new DatabaseHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_agenda_lista, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewInformation(getView()).show();
            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        Calendar cal = Calendar.getInstance();
        String dateCurrent;
        if ((cal.get(Calendar.MONTH) + 1) < 10) {
            if(cal.get(Calendar.DAY_OF_MONTH) < 10) {
                dateCurrent = "0" + cal.get(Calendar.DAY_OF_MONTH) + "/" + "0" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
            } else {
                dateCurrent = cal.get(Calendar.DAY_OF_MONTH) + "/" + "0" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
            }
        } else {
            if(cal.get(Calendar.DAY_OF_MONTH) < 10) {
                dateCurrent = "0" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.YEAR);
            } else {
                dateCurrent = cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.YEAR);
            }
        }


        ArrayList<Information> informations = database.selectAgendaInformationByData(dateCurrent);

        adapter = new AgendaAdapter(getActivity(), informations);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(getActivity(), "onClick " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

                TextView empresaTextView = (TextView) view.findViewById(R.id.empresa);
                TextView empresaDestinyTextView = (TextView) view.findViewById(R.id.empresaDestiny);
                TextView notaFiscalTextView = (TextView) view.findViewById(R.id.notaFiscal);
                TextView pesoTextView = (TextView) view.findViewById(R.id.peso);
                TextView valorTextView = (TextView) view.findViewById(R.id.valor);

                String empresaTextViewFormated = empresaTextView.getText().toString().substring(9, empresaTextView.getText().toString().length());
                String empresaDestinyTextViewFormated = empresaDestinyTextView.getText().toString().substring(9, empresaDestinyTextView.getText().toString().length());
                String pesoTextViewFormated = pesoTextView.getText().toString().replaceAll("[Kg]", "").replaceAll("[Peso:]","").trim();
                String notaFiscalTextFormated  = notaFiscalTextView.getText().toString().replaceAll("[NF]", "").trim();
                String valorTextViewFormated = valorTextView.getText().toString().replaceAll("[Valor:]","").trim();

                Information informationDelete = new Information(CalendarFragment.data, empresaTextViewFormated, empresaDestinyTextViewFormated, notaFiscalTextFormated, pesoTextViewFormated, valorTextViewFormated, 0);

                AlertDialog askIt = askIfWantsToDelete(informationDelete);
                askIt.show();
            }
        }));

        return view;
    }

    private AlertDialog.Builder createNewInformation(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Frete & Cif:");

        // Set up the input
        final EditText empresaDestiny = new EditText(getActivity());
        final EditText notaFiscal = new EditText(getActivity());
        final EditText peso = new EditText(getActivity());
        final EditText valor = new EditText(getActivity());

        final RadioGroup radioGroup = new RadioGroup(view.getContext());
        final RadioButton radioButtonAdecol = new RadioButton(view.getContext());
        final RadioButton radioButtonCartint = new RadioButton(view.getContext());
        final CheckBox checkBoxAdecol = new CheckBox(view.getContext());

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        empresaDestiny.setInputType(InputType.TYPE_CLASS_TEXT);
        empresaDestiny.setHint("Empresa de Destino");
        notaFiscal.setInputType(InputType.TYPE_CLASS_PHONE);
        notaFiscal.setHint("Nota Fiscal");
        peso.setInputType(InputType.TYPE_CLASS_PHONE);
        peso.setHint("Peso");
        valor.setInputType(InputType.TYPE_CLASS_PHONE);
        valor.setHint("Valor");
        valor.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){
                    valor.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[R$,.]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    current = formatted;
                    valor.setText(formatted);
                    valor.setSelection(formatted.length());

                    valor.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        radioButtonAdecol.setText("Adecol");
        radioButtonCartint.setText("Cartint");
        checkBoxAdecol.setText("Coleta Joceliane");
        radioButtonAdecol.setTextSize(17);
        radioButtonCartint.setTextSize(17);
        radioGroup.addView(radioButtonAdecol);
        radioGroup.addView(radioButtonCartint);
        radioGroup.check(radioGroup.getChildAt(0).getId());

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);

                if(checkedRadioButton == radioButtonAdecol) {
                    checkBoxAdecol.setVisibility(View.VISIBLE);
                } else {
                    checkBoxAdecol.setVisibility(View.GONE);
                }

            }
        });

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(radioGroup);
        linearLayout.addView(checkBoxAdecol);
        linearLayout.addView(empresaDestiny);
        linearLayout.addView(notaFiscal);
        linearLayout.addView(peso);
        linearLayout.addView(valor);

        builder.setView(linearLayout);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String empresa;
                int isColetaJoceliane;
                if(radioButtonAdecol.isChecked()) {
                    empresa = "Adecol";
                    if(checkBoxAdecol.isChecked()) {
                        isColetaJoceliane = 1;
                    } else {
                        isColetaJoceliane = 0;
                    }
                } else {
                    empresa = "Cartint";
                    isColetaJoceliane = 0;
                }
                database.insertAgendaInformation(new Information(CalendarFragment.data, empresa, empresaDestiny.getText().toString(), notaFiscal.getText().toString(), peso.getText().toString(), valor.getText().toString(), isColetaJoceliane));
                AgendaAdapter.dataInfo = database.selectAgendaInformationByData(CalendarFragment.data);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder;

    }

    private AlertDialog askIfWantsToDelete(final Information informationToDeleteDialog) {

        return new AlertDialog.Builder(getActivity())
                //set message, title, and icon
                .setTitle("Deletar")
                .setMessage("Deseja continuar com a deleção da informação?")

                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        database.deleteAgendaInformation(informationToDeleteDialog);
                        AgendaAdapter.dataInfo = database.selectAgendaInformationByData(CalendarFragment.data);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if(child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if(child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildLayoutPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }

    public abstract static class ClickListener {
        public abstract void onClick(View view, int position);
        public abstract void onLongClick(View view, int position);
    }

}
