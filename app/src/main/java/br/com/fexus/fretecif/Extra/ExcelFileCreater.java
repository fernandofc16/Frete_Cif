package br.com.fexus.fretecif.Extra;

import android.content.Context;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.fexus.fretecif.Database.DatabaseHandler;

public class ExcelFileCreater {

    private File file;
    private DatabaseHandler database;
    private List<String> datasFromTo;
    private String company;
    private BigDecimal valorTotal = new BigDecimal(0.0);
    Workbook workbook;
    Sheet sheet;

    public ExcelFileCreater(File file, Context context, String dataFrom, String dataTo, String company) {
        this.file = file;
        this.company = company;
        database = new DatabaseHandler(context);
        datasFromTo = DateCount.diferencaDeDatas(dataFrom, dataTo);
    }

    public boolean createExcelFile() {

        boolean criou = true;
        workbook = new HSSFWorkbook();

        try {
            if(!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName("Frete&Cif e Coletas"));
        //sheet.setColumnWidth(0, 5000 );
        sheet.addMergedRegion(new CellRangeAddress(/*  Row : 1-1 */0, 0, /* Column : A-E */0, 4));
        sheet.setDefaultColumnWidth(20);

        // Title
        Row row1 = sheet.createRow(0);
        Cell cellTitle = row1.createCell(0);
        cellTitle.setCellValue("FRETE&CIF e COLETAS");
        CellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setAlignment(CellStyle.ALIGN_CENTER);
        Font fontTitle = workbook.createFont();
        fontTitle.setBold(true);
        styleTitle.setFont(fontTitle);
        cellTitle.setCellStyle(styleTitle);
        // Title

        // INFORMAÇÕES - 2 linha
        CellStyle styleRows2 = workbook.createCellStyle();
        styleRows2.setAlignment(CellStyle.ALIGN_CENTER);
        Row row2 = sheet.createRow(1);

        Cell cellRow2DATA = row2.createCell(0);
        cellRow2DATA.setCellValue("DATA");
        cellRow2DATA.setCellStyle(styleRows2);

        Cell cellRow2EMPRESA = row2.createCell(1);
        cellRow2EMPRESA.setCellValue("EMPRESA");
        cellRow2EMPRESA.setCellStyle(styleRows2);

        Cell cellRow2PESO = row2.createCell(2);
        cellRow2PESO.setCellValue("PESO (KG)");
        cellRow2PESO.setCellStyle(styleRows2);

        Cell cellRow2NOTAFISCAL = row2.createCell(3);
        cellRow2NOTAFISCAL.setCellValue("NOTA FISCAL");
        cellRow2NOTAFISCAL.setCellStyle(styleRows2);

        Cell cellRow2VALOR = row2.createCell(4);
        cellRow2VALOR.setCellValue("VALOR");
        cellRow2VALOR.setCellStyle(styleRows2);

        List<Information> informationsByDates = new ArrayList<>();
        for (String data : datasFromTo) {

            for (Information info : database.selectAgendaInformationByDataAndCompany(data, company)) {
                informationsByDates.add(info);
            }
        }

        for(int i = 0; i < informationsByDates.size(); i++) {
            Row rowInfo = sheet.createRow(i + 2);
            for (int j = 0; j < 5; j++) {
                Cell cellInfo = rowInfo.createCell(j);
                switch (j) {
                    case 0:
                        cellInfo.setCellValue(informationsByDates.get(i).getData());
                        break;
                    case 1:
                        cellInfo.setCellValue(informationsByDates.get(i).getEmpresaDestiny());
                        break;
                    case 2:
                        cellInfo.setCellValue(informationsByDates.get(i).getPeso());
                        break;
                    case 3:
                        cellInfo.setCellValue("NF".concat(informationsByDates.get(i).getNotaFiscal()));
                        break;
                    case 4:
                        String valor = informationsByDates.get(i).getValor();
                        cellInfo.setCellValue(valor);
                        if(!valor.trim().equals("")) {
                            String valorFormated = valor.replaceAll("[R$]", "");
                            String valorFormated1 = valorFormated.replaceAll("\\.", "");
                            String valorFormated2 = valorFormated1.replaceAll(",", ".");
                            BigDecimal valorFormatedBigDouble = BigDecimal.valueOf(Double.parseDouble(valorFormated2));
                            valorTotal = valorTotal.add(valorFormatedBigDouble);
                        }
                        break;
                }
            }
        }

        Row rowValorTotal = sheet.createRow(informationsByDates.size() + 2);

        Cell cellValorTotalText = rowValorTotal.createCell(3);
        cellValorTotalText.setCellValue("Valor Total:");

        Cell cellValorTotal = rowValorTotal.createCell(4);
        String valorTotalString = String.valueOf((valorTotal)).replaceAll("\\.", ",");
        cellValorTotal.setCellValue("R$" + valorTotalString);

        CellStyle styleValorFinal = workbook.createCellStyle();
        styleValorFinal.setAlignment(CellStyle.ALIGN_CENTER);

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

}
