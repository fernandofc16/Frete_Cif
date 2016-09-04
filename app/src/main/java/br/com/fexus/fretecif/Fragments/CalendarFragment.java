package br.com.fexus.fretecif.Fragments;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.fexus.fretecif.Adapters.AgendaAdapter;
import br.com.fexus.fretecif.Database.DatabaseHandler;
import br.com.fexus.fretecif.Extra.DateCount;
import br.com.fexus.fretecif.Extra.ExcelFileCreater;
import br.com.fexus.fretecif.R;

public class CalendarFragment extends Fragment {

    private DatabaseHandler database;
    private AlertDialog.Builder builder;
    public static String data;
    private String current = "";
    private Calendar cal = Calendar.getInstance();
    private TabLayout tabLayout;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new DatabaseHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        tabLayout = (TabLayout) getActivity().findViewById(R.id.tabLayout);

        CalendarView calendar = (CalendarView) view.findViewById(R.id.calendar);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        data = sdf.format(new Date(calendar.getDate()));
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                int monthInt = (month + 1);
                if (monthInt < 10) {
                    if(dayOfMonth < 10) {
                        data = "0" + dayOfMonth + "/" + "0" + monthInt + "/" + year;
                    } else {
                        data = dayOfMonth + "/" + "0" + monthInt + "/" + year;
                    }
                } else {
                    if(dayOfMonth < 10) {
                        data = "0" + dayOfMonth + "/" + monthInt + "/" + year;
                    } else {
                        data = dayOfMonth + "/" + monthInt + "/" + year;
                    }
                }

                AgendaAdapter.dataInfo = database.selectAgendaInformationByData(CalendarFragment.data);
                AgendaListaFragment.adapter.notifyDataSetChanged();

                TabLayout.Tab tab = tabLayout.getTabAt(1);
                tab.select();

            }
        });

        final Button backUpSQlite = (Button) view.findViewById(R.id.backUpSQlite);
        backUpSQlite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File backUpFile = exportDB();

                if(backUpFile.length() != 0) {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("application/octet-stream");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"fernandofc16@gmail.com"});

                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Backup Informações Frete&Cif - Coleta");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(backUpFile));

                    getActivity().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }

            }
        });

        Button calculateTotal = (Button) view.findViewById(R.id.calculateTotal);
        calculateTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
                builder.setTitle("Empresa:");

                LinearLayout layout = new LinearLayout(v.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText dateFrom = new EditText(v.getContext());

                final RadioGroup radioGroup = new RadioGroup(v.getContext());
                final RadioButton radioButtonAdecol = new RadioButton(v.getContext());
                final RadioButton radioButtonCartint = new RadioButton(v.getContext());

                dateFrom.setHint("Do dia...");
                dateFrom.setInputType(InputType.TYPE_CLASS_NUMBER);
                dateFrom.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        int sel = onTextChangedDates(dateFrom, s);
                        dateFrom.setSelection(sel < current.length() ? sel : current.length());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                final EditText dateTo = new EditText(v.getContext());
                dateTo.setHint("Até o dia...");
                dateTo.setInputType(InputType.TYPE_CLASS_NUMBER);
                dateTo.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        int sel = onTextChangedDates(dateTo, s);
                        dateTo.setSelection(sel < current.length() ? sel : current.length());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                radioButtonAdecol.setText("Adecol");
                radioButtonCartint.setText("Cartint");
                radioButtonAdecol.setTextSize(17);
                radioButtonCartint.setTextSize(17);
                radioGroup.addView(radioButtonAdecol);
                radioGroup.addView(radioButtonCartint);
                radioGroup.check(radioGroup.getChildAt(0).getId());

                layout.addView(radioGroup);
                layout.addView(dateFrom);
                layout.addView(dateTo);

                builder.setView(layout);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int item = -1;

                        if(radioButtonAdecol.isChecked()) {
                            item = 1;
                        } else if(radioButtonCartint.isChecked()) {
                            item = 0;
                        }

                        List<String> datas = DateCount.diferencaDeDatas(dateFrom.getText().toString(), dateTo.getText().toString());

                        BigDecimal result = (database.calculateTotalByCompany(item, datas));
                        AlertDialog.Builder builderResult = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
                        builderResult.setTitle("Resultado:");
                        String resultFirst = String.valueOf(result);
                        String[] resultsSize = resultFirst.split("\\.");
                        if(resultsSize.length >= 2) {
                            if (resultsSize[1].length() < 2) {
                                resultFirst = resultFirst + "0";
                            }
                        }
                        String resultSecond = resultFirst.replaceAll("\\.", ",");

                        builderResult.setMessage("R$" + resultSecond);
                        builderResult.show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });

        Button sendXlsEmail = (Button) view.findViewById(R.id.sendXlsFileToEmail);
        sendXlsEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File folder = getActivity().getExternalFilesDir("Frete&Cif");
                File file = new File(folder, "Frete&Cif.xls");
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {""});

                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT,  "");
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

                getActivity().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });

        Button openXLS = (Button) view.findViewById(R.id.openXLS);
        openXLS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openXLSX();
            }
        });

        Button creatDocumentButton = (Button) view.findViewById(R.id.creatDocumentExcel);
        creatDocumentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
                builder.setTitle("Criar arquivo Excel");

                LinearLayout layout = new LinearLayout(v.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                // Set up the input
                final EditText dateFrom = new EditText(v.getContext());
                dateFrom.setHint("Do dia...");
                dateFrom.setInputType(InputType.TYPE_CLASS_NUMBER);
                dateFrom.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        int sel = onTextChangedDates(dateFrom, s);
                        dateFrom.setSelection(sel < current.length() ? sel : current.length());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                final EditText dateTo = new EditText(v.getContext());
                dateTo.setHint("Até o dia...");
                dateTo.setInputType(InputType.TYPE_CLASS_NUMBER);
                dateTo.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        int sel = onTextChangedDates(dateTo, s);
                        dateTo.setSelection(sel < current.length() ? sel : current.length());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                layout.addView(dateFrom);
                layout.addView(dateTo);

                // Put input on view
                builder.setView(layout);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File folder = getActivity().getExternalFilesDir("Frete&Cif");
                        File file = new File(folder, "Frete&Cif.xls");
                        ExcelFileCreater excelFileCreater = new ExcelFileCreater(file, getActivity(), dateFrom.getText().toString(), dateTo.getText().toString());
                        boolean criou = excelFileCreater.createExcelFile();
                        if(criou) {
                            Toast.makeText(getActivity(), "Arquivo criado com sucesso", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Falha ao criar arquivo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });

        return view;

    }

    public void openXLSX() {
        File folder = getActivity().getExternalFilesDir("Frete&Cif");
        File file = new File(folder, "Frete&Cif.xls");
        if(file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(Intent.createChooser(intent, "OPEN FILE"));
        } else {
            Toast.makeText(getActivity(), "Arquivo não encontrado", Toast.LENGTH_LONG).show();
        }
    }

    private int onTextChangedDates(TextView dateText, CharSequence s) {
        int sel = 0;
        if (!s.toString().equals(current)) {
            String clean = s.toString().replaceAll("[^\\d.]", "");
            String cleanC = current.replaceAll("[^\\d.]", "");

            int cl = clean.length();
            sel = cl;
            for (int i = 2; i <= cl && i < 6; i += 2) {
                sel++;
            }
            //Fix for pressing delete next to a forward slash
            if (clean.equals(cleanC)) sel--;

            if (clean.length() < 8){
                String ddmmyyyy = "DDMMYYYY";
                clean = clean + ddmmyyyy.substring(clean.length());
            }else{
                //This part makes sure that when we finish entering numbers
                //the date is correct, fixing it otherwise
                int day  = Integer.parseInt(clean.substring(0,2));
                int mon  = Integer.parseInt(clean.substring(2,4));
                int year = Integer.parseInt(clean.substring(4,8));

                if(mon > 12) mon = 12;
                cal.set(Calendar.MONTH, mon-1);
                year = (year<1900)?1900:(year>2100)?2100:year;
                cal.set(Calendar.YEAR, year);
                // ^ first set year for the line below to work correctly
                //with leap years - otherwise, date e.g. 29/02/2012
                //would be automatically corrected to 28/02/2012

                day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                clean = String.format("%02d%02d%02d",day, mon, year);
            }

            clean = String.format("%s/%s/%s", clean.substring(0, 2),
                    clean.substring(2, 4),
                    clean.substring(4, 8));

            sel = sel < 0 ? 0 : sel;
            current = clean;
            dateText.setText(current);
        }
        return sel;
    }

    class MyBackupAgent extends BackupAgentHelper {
        private static final String DB_NAME = "agendaManager";

        @Override
        public void onCreate(){
            FileBackupHelper dbs = new FileBackupHelper(this, DB_NAME);
            addHelper("dbs", dbs);
        }

        @Override
        public File getFilesDir(){
            File path = getActivity().getDatabasePath(DB_NAME);
            return path.getParentFile();
        }
    }

    private void importDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "<package name>"
                        + "//databases//" + "<database name>";
                String backupDBPath = "<backup db filename>"; // From SD directory.
                File backupDB = new File(data, currentDBPath);
                File currentDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getActivity(), "Import Successful!",
                        Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {

            Toast.makeText(getActivity(), "Import Failed!", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    private File exportDB() {

        File backUpDB = new File("");

        try {
            File backUpFolder = new File(getActivity().getExternalFilesDir("Frete&Cif").getAbsolutePath() + "/databaseBackUp");
            File data = getActivity().getDatabasePath("agendaManager");

            Log.e("FEXUS: ", data.getAbsolutePath());

            if(!backUpFolder.exists()) {
                backUpFolder.mkdir();
            }


            File files[] = data.listFiles();

            for (File file : files) {
                Log.e("FEXUS: ", file.getName());
            }

                //String currentDBPath = "/data/" + "Frete&Cif" + "/databases/" + "agendaManager.db";
                // currentDB = new File(data, currentDBPath);
                backUpDB = new File(backUpFolder, "backUpSQLiteFreteCif.db");

                FileChannel src = new FileInputStream(data).getChannel();
                FileChannel dst = new FileOutputStream(backUpDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Log.e("FEXUS: ", "BACKUP REALIZADO");

        } catch (Exception e) {

            e.printStackTrace();
            Log.e("FEXUS: ", "BACKUP NÃO REALIZADO");

        }

        Log.e("FEXUS: ", "PASSOU PELO EXPORT");
        return backUpDB;

    }

}
