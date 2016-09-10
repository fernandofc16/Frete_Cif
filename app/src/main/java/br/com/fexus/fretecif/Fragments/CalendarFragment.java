package br.com.fexus.fretecif.Fragments;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.fexus.fretecif.Adapters.AgendaAdapter;
import br.com.fexus.fretecif.Database.DatabaseHandler;
import br.com.fexus.fretecif.R;

public class CalendarFragment extends Fragment {

    private DatabaseHandler database;
    public static String data;
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
                if (tab != null) {
                    tab.select();
                }

            }
        });

        return view;

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
/*
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
    }*/

}
