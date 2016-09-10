package br.com.fexus.fretecif.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.fexus.fretecif.Extra.Information;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_NAME = "agendaManager",
                                TABLE_INFORMATION = "agendaInformation",
                                KEY_ID = "id",
                                KEY_DATA = "data",
                                KEY_EMPRESA = "empresa",
                                KEY_EMPRESA_DESTINY = "empresaDestiny",
                                KEY_NOTA_FISCAL = "notaFiscal",
                                KEY_PESO = "peso",
                                KEY_VALOR = "valor",
                                KEY_IS_COLETA_JUCELIANE = "isColetaJuceliane";

    private static SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_INFORMATION + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_DATA + " VARCHAR(255),"
                + KEY_EMPRESA + " VARCHAR(255),"
                + KEY_EMPRESA_DESTINY + " VARCHAR(255),"
                + KEY_NOTA_FISCAL + " VARCHAR(255),"
                + KEY_PESO + " VARCHAR(255),"
                + KEY_VALOR + " VARCHAR(255),"
                + KEY_IS_COLETA_JUCELIANE + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INFORMATION);
        onCreate(db);
    }

    public long insertAgendaInformation(Information agendaInformation) {
        database = getWritableDatabase();

        ContentValues values =  new ContentValues();
        values.put(KEY_DATA, agendaInformation.getData());
        values.put(KEY_EMPRESA, agendaInformation.getEmpresa());
        values.put(KEY_EMPRESA_DESTINY, agendaInformation.getEmpresaDestiny());
        values.put(KEY_NOTA_FISCAL, agendaInformation.getNotaFiscal());
        values.put(KEY_PESO, agendaInformation.getPeso());
        values.put(KEY_VALOR, agendaInformation.getValor());
        values.put(KEY_IS_COLETA_JUCELIANE, agendaInformation.isColetaJuciliane());

        long id = database.insert(TABLE_INFORMATION, null, values);
        database.close();

        return id;
    }

    public ArrayList<Information> selectAgendaInformationByData(String data) {
        database = getReadableDatabase();

        Cursor cursor = database.query(TABLE_INFORMATION, null, KEY_DATA + "=?", new String[] { data }, null, null, KEY_IS_COLETA_JUCELIANE + " DESC, " + KEY_EMPRESA + " ASC", null);
        //Cursor query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit);
        //table = name of Table
        //columns = list of columns to process, null returns all

        ArrayList<Information> information = new ArrayList<>();

        while(cursor.moveToNext()) {
            information.add(new Information(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7)));
        }

        cursor.close();
        database.close();
        return information;
    }

    public ArrayList<Information> selectAgendaInformationByDataAndCompany(String data, String company) {
        database = getReadableDatabase();

        Cursor cursor = database.query(TABLE_INFORMATION, null, KEY_DATA + "=? AND " + KEY_EMPRESA + "=?", new String[] { data, company }, null, null, null, null);
        //Cursor query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit);
        //table = name of Table
        //columns = list of columns to process, null returns all

        ArrayList<Information> information = new ArrayList<>();

        while(cursor.moveToNext()) {
            information.add(new Information(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7)));
        }

        cursor.close();
        database.close();
        return information;
    }

    public ArrayList<Information> selectAgendaInformationFromJoceliane(String data) {
        database = getReadableDatabase();

        Cursor cursor = database.query(TABLE_INFORMATION, null, KEY_DATA + "=? AND " + KEY_IS_COLETA_JUCELIANE + "=?", new String[] { data, "1" }, null, null, null, null);
        //Cursor query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit);
        //table = name of Table
        //columns = list of columns to process, null returns all

        ArrayList<Information> information = new ArrayList<>();

        while(cursor.moveToNext()) {
            information.add(new Information(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7)));
        }

        cursor.close();
        database.close();
        return information;
    }

    public boolean deleteAgendaInformation(Information agendaInformation) {
        database =  getWritableDatabase();
        int deletou = database.delete(TABLE_INFORMATION, KEY_DATA + "=? AND " + KEY_EMPRESA + "=? AND " + KEY_EMPRESA_DESTINY + "=? AND " + KEY_NOTA_FISCAL + "=? AND " + KEY_PESO + "=? AND " + KEY_VALOR + "=?",
                new String[]{agendaInformation.getData(), agendaInformation.getEmpresa(), agendaInformation.getEmpresaDestiny(), agendaInformation.getNotaFiscal(), agendaInformation.getPeso(), agendaInformation.getValor()});
        database.close();
        return deletou > 0;
    }

    public BigDecimal calculateTotalByCompany(int company, List<String> datas) {

        database = getReadableDatabase();

        BigDecimal total = new BigDecimal(0.0);
        String companyName = "";

        switch (company) {
            case 0:
                companyName = "Cartint";
                break;
            case 1:
                companyName = "Adecol";
                break;
        }

        for (String data : datas) {

            Cursor cursorCartint = database.query(TABLE_INFORMATION, null, KEY_DATA + "=? AND " + KEY_EMPRESA + "=?", new String[]{data, companyName}, null, null, null);
            while (cursorCartint.moveToNext()) {
                String valor = cursorCartint.getString(6);
                String valorFormated = valor.replaceAll("[R$]", "");
                String valorFormated1 = valorFormated.replaceAll("\\.", "");
                String valorFormated2 = valorFormated1.replaceAll(",", ".");
                total = total.add(BigDecimal.valueOf(Double.parseDouble(valorFormated2)));
            }
            cursorCartint.close();

        }


        database.close();

        return total;

    }

}
