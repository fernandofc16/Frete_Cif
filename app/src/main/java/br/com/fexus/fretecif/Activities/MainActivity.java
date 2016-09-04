package br.com.fexus.fretecif.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import br.com.fexus.fretecif.Adapters.TabsAdapterMain;
import br.com.fexus.fretecif.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CalendarView calendar = null;
    File file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        File folder = getExternalFilesDir("Frete&Cif");

        file = new File(folder, "Frete&Cif.xls");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabsAdapterMain tabsAdapterMain = new TabsAdapterMain(getSupportFragmentManager());

        viewPager.setAdapter(tabsAdapterMain);
        tabLayout.setupWithViewPager(viewPager);

    }

    public void openXLSX(View view) {
        if(file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(Intent.createChooser(intent, "OPEN FILE"));
        } else {
            Toast.makeText(getApplicationContext(), "No File Found", Toast.LENGTH_LONG).show();
        }
    }

    public void printDirectory(View view) {
        Toast.makeText(getApplicationContext(), file.getAbsolutePath(), Toast.LENGTH_LONG).show();
    }

    public void deleteFile(View view) {
        if (file.exists()) {
            file.delete();
            Toast.makeText(getApplicationContext(), "File Deleted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No File Found", Toast.LENGTH_LONG).show();
        }
    }

    private boolean createExcelFile() {
        boolean criou = true;
        Workbook workbook = new HSSFWorkbook();

        try {
            if(!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Sheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName("Frete&Cif"));
        sheet.setColumnWidth(0, 5000 );
        sheet.addMergedRegion(new CellRangeAddress(/*  Row : 1-1 */0, 0, /* Column : A-F */0, 5));

        Row row = sheet.createRow(0);
        row.setHeightInPoints(30);

        Cell cell = row.createCell(0);
        cell.setCellValue("Cell:A1");
        Cell cell2 = sheet.createRow(1).createCell(0);
        cell2.setCellValue("Cell:A2");

        CellStyle style = workbook.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.TURQUOISE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);

        style.setAlignment(CellStyle.ALIGN_CENTER);

        Font font = workbook.createFont();
        font.setColor(IndexedColors.CORAL.getIndex());
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setItalic(true);
        font.setFontHeightInPoints((short) 20);
        font.setUnderline(Font.U_DOUBLE);
        font.setFontName("Times New Roman");

        style.setFont(font);
        cell.setCellStyle(style);

        Row row2 = sheet.createRow(2);
        Cell cellA3 = row2.createCell(0);
        cellA3.setCellValue(15);
        //cellC1.setCellStyle(arg0);
        Cell cellB3 = row2.createCell(1);
        cellB3.setCellValue("+");
        Cell cellC3 = row2.createCell(2);
        cellC3.setCellValue(15);
        Cell cellD3 = row2.createCell(3);
        cellD3.setCellValue("=");
        Cell cellE3 = row2.createCell(4);
        cellE3.setCellFormula("A3+C3");

        FileOutputStream output = null;

        try {

            output = new FileOutputStream(file);
            workbook.write(output);

        } catch (Exception e) {
            e.printStackTrace();
            criou = false;
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return criou;
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

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
