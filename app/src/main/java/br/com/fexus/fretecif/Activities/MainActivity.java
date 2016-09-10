package br.com.fexus.fretecif.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
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
import java.util.Calendar;
import java.util.List;

import br.com.fexus.fretecif.Adapters.TabsAdapterMain;
import br.com.fexus.fretecif.Database.DatabaseHandler;
import br.com.fexus.fretecif.Extra.DateCount;
import br.com.fexus.fretecif.Extra.ExcelFileCreater;
import br.com.fexus.fretecif.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String current = "";
    private Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //File folder = getExternalFilesDir("Frete&Cif");

        //File file = new File(folder, "Frete&Cif.xls");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setStatusBarBackgroundColor(5);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //Toast.makeText(getApplicationContext(),"Drawer Opened",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //Toast.makeText(getApplicationContext(),"Drawer Closed",Toast.LENGTH_SHORT).show();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setFitsSystemWindows(true);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabsAdapterMain tabsAdapterMain = new TabsAdapterMain(getSupportFragmentManager());

        viewPager.setAdapter(tabsAdapterMain);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.creatDocumentExcel) {

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AppTheme));
            builder.setTitle("Criar arquivo Excel");

            LinearLayout layout = new LinearLayout(MainActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);

            final RadioGroup radioGroup = new RadioGroup(MainActivity.this);
            final RadioButton radioButtonAdecol = new RadioButton(MainActivity.this);
            final RadioButton radioButtonCartint = new RadioButton(MainActivity.this);
            final CheckBox checkBoxAdecol = new CheckBox(MainActivity.this);

            // Set up the input
            final EditText dateFrom = new EditText(MainActivity.this);
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

            final EditText dateTo = new EditText(MainActivity.this);
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
            checkBoxAdecol.setText("Somente Coletas Joceliane");
            radioButtonAdecol.setTextSize(17);
            radioButtonCartint.setTextSize(17);
            radioGroup.addView(radioButtonAdecol);
            radioGroup.addView(radioButtonCartint);
            radioGroup.check(radioGroup.getChildAt(0).getId());

            layout.addView(radioGroup);
            layout.addView(checkBoxAdecol);
            layout.addView(dateFrom);
            layout.addView(dateTo);

            // Put input on view
            builder.setView(layout);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File folder = getExternalFilesDir("Frete&Cif");
                    File file = new File(folder, "Frete&Cif.xls");
                    String company;
                    if (radioButtonAdecol.isChecked()) {
                        company = "Adecol";
                    } else {
                        company = "Cartint";
                    }
                    ExcelFileCreater excelFileCreater = new ExcelFileCreater(file, getApplicationContext(), dateFrom.getText().toString(), dateTo.getText().toString(), company);

                    boolean criou = excelFileCreater.createExcelFile(checkBoxAdecol.isChecked());

                    if (criou) {
                        Toast.makeText(getApplicationContext(), "Arquivo criado com sucesso", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Falha ao criar arquivo", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();

        } else if (id == R.id.openXLS) {

            openXLSX();

        } else if (id == R.id.sendXlsFileToEmail) {

            File folder = getExternalFilesDir("Frete&Cif");
            File file = new File(folder, "Frete&Cif.xls");
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});

            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

            startActivity(Intent.createChooser(emailIntent, "Send mail..."));

        } else if (id == R.id.calculateTotal) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AppTheme));
            builder.setTitle("Empresa:");

            LinearLayout layout = new LinearLayout(MainActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText dateFrom = new EditText(MainActivity.this);

            final RadioGroup radioGroup = new RadioGroup(MainActivity.this);
            final RadioButton radioButtonAdecol = new RadioButton(MainActivity.this);
            final RadioButton radioButtonCartint = new RadioButton(MainActivity.this);

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

            final EditText dateTo = new EditText(MainActivity.this);
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
                    DatabaseHandler database = new DatabaseHandler(getApplicationContext());
                    int item = -1;

                    if (radioButtonAdecol.isChecked()) {
                        item = 1;
                    } else if (radioButtonCartint.isChecked()) {
                        item = 0;
                    }

                    List<String> datas = DateCount.diferencaDeDatas(dateFrom.getText().toString(), dateTo.getText().toString());

                    BigDecimal result = (database.calculateTotalByCompany(item, datas));
                    AlertDialog.Builder builderResult = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AppTheme));
                    builderResult.setTitle("Resultado:");
                    String resultFirst = String.valueOf(result);
                    String[] resultsSize = resultFirst.split("\\.");
                    if (resultsSize.length >= 2) {
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

        } else if (id == R.id.backUpSQlite) {

            exportDB();
            /*
            if(backUpFile.length() != 0) {

            } else {
                Toast.makeText(getApplicationContext(), "File Backup is empty", Toast.LENGTH_LONG).show();
            }*/

        } else if (id == R.id.importSQLite) {

            importDB();

        } else if (id == R.id.sendSQLite) {

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("application/octet-stream");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"fernandofc16@gmail.com"});

            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Backup Agenda - Frete&Cif e Coleta");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/BackUpAgendaManagerSQLite")));

            startActivity(Intent.createChooser(emailIntent, "Send mail..."));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //exporting database
    private void exportDB() {
        // TODO Auto-generated method stub

        try {

            File sd2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            if (sd2.canWrite()) {

                String backupDBPath = "/BackUpAgendaManagerSQLite";

                File currentDB = getApplicationContext().getDatabasePath("agendaManager");
                File backupDB = new File(sd2, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getApplicationContext(), backupDB.toString(), Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getApplicationContext(), "SD can't write", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            Log.e("FEXUS", e.toString());

        }
    }

    private void importDB() {

        try {

            File sd2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            if (sd2.canWrite()) {

                String backupDBPath = "/BackUpAgendaManagerSQLite";

                File backupDB = new File(sd2, backupDBPath);
                File currentDB = getApplicationContext().getDatabasePath("agendaManager");

                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), backupDB.toString(), Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();

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

            if (clean.length() < 8) {
                String ddmmyyyy = "DDMMYYYY";
                clean = clean + ddmmyyyy.substring(clean.length());
            } else {
                //This part makes sure that when we finish entering numbers
                //the date is correct, fixing it otherwise
                int day = Integer.parseInt(clean.substring(0, 2));
                int mon = Integer.parseInt(clean.substring(2, 4));
                int year = Integer.parseInt(clean.substring(4, 8));

                if (mon > 12) mon = 12;
                cal.set(Calendar.MONTH, mon - 1);
                year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                cal.set(Calendar.YEAR, year);
                // ^ first set year for the line below to work correctly
                //with leap years - otherwise, date e.g. 29/02/2012
                //would be automatically corrected to 28/02/2012

                day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                clean = String.format("%02d%02d%02d", day, mon, year);
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

    public void openXLSX() {
        File folder = getExternalFilesDir("Frete&Cif");
        File file = new File(folder, "Frete&Cif.xls");
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(Intent.createChooser(intent, "OPEN FILE"));
        } else {
            Toast.makeText(getApplicationContext(), "Arquivo não encontrado", Toast.LENGTH_LONG).show();
        }
    }

}
