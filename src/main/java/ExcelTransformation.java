import java.io.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.util.POILogger;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;

public class ExcelTransformation {

    public static List<String> headers = Arrays.asList("Item", "Amount", "Nutrients", "Nutrient Amount" ,  "% Daily Recc.");
    //controls the number of decimal places to format any numbers
    public static DecimalFormat df = new DecimalFormat("#.#");

    public static void writeDataToExcel(List<Map<String, Object>> data) throws FileNotFoundException {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("vitamins");
        createHeader(sheet);
        int currentRow = 1;
        for (Map<String,Object> food: data) {
            currentRow=createRow(currentRow,sheet, food);
        }

        try (OutputStream fileOut = new FileOutputStream("vitamin.xlsx")) {
            wb.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void createHeader(XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(0);
        int i =0;
        for (String header : headers) {
            row.createCell(i).setCellValue(header);
            i++;
        }

    }

    public static int createRow(int currentRow, XSSFSheet sheet,Map<String,Object> food){
        try {
            boolean firstRow = true;
            int initialRow = currentRow;
            if (!nutrientConstants.nutrientDailyAmount.isEmpty()) {
                List<Map<String,Object>> foodNutrients = (List<Map<String,Object>>)food.get("foodNutrients");
                for (String nutrient : nutrientConstants.nutrients) {
                    Map<String, Object> currentNutrient = findNutrient(foodNutrients, nutrient);
                    //currentRow++;
                    //nutrientConstants.nutrientDailyAmount.put(entry.getKey(), entry.getValue());
                    XSSFRow row = sheet.createRow(currentRow); //create a row per nutrient
                    if (firstRow) {
                        //CellStyle cellStyle = row.getSheet().getWorkbook().createCellStyle();
                        //cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                        row.createCell(0).setCellValue((String) food.get("description"));
                        row.createCell(1).setCellValue("100 g");
                        //row.getCell(0).setCellStyle(cellStyle);
                        //row.getCell(1).setCellStyle(cellStyle);
                        firstRow = false;
                    }
                    row.createCell(2).setCellValue(nutrient.replaceAll("_", " ").replaceAll("Total lipid", "Fat")); //populate the 3rd cell with the label
                    if (currentNutrient != null) {
                        //fixUnits(currentNutrient);
                        row.createCell(3).setCellValue(df.format(currentNutrient.get("amount")) + ((String)currentNutrient.get("unitName")).toLowerCase()); //concanenante unit of measure
                    }
                    if (nutrientConstants.nutrientDailyAmount.containsKey(nutrient) && currentNutrient != null) {
                        //we only need to adjust the units when we are calculating daily %
                        fixUnits(currentNutrient, nutrient);
                        row.createCell(4).setCellValue(calculateDailyRec(nutrient, (Double) currentNutrient.get("amount")));
                    }
                    currentRow++;
                }
                formatCells(sheet, initialRow , currentRow-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentRow;
    }

    private static Map<String,Object> findNutrient(List<Map<String,Object>> foodNutrients, String nutrient) {
        for (Map<String, Object> map : foodNutrients)
        {
            if(((String)map.get("name")).contains(nutrient.replaceAll("_"," "))) {
                return map;
            }
        }
        return null;
    }

    private static String calculateDailyRec(String food, Double nutrientAmount) {
        if (nutrientConstants.nutrientDailyAmount.containsKey(food)) {
            Double dailyAmount = Double.parseDouble(nutrientConstants.nutrientDailyAmount.get(food));
            Double percentAmount =( nutrientAmount / dailyAmount  )* 100;
            return df.format(percentAmount) + "%";
        }
        return "";
    }
    //UOM = Unit of Measure
    private static void fixUnits(Map<String,Object> nutrient, String nutrientName) {
        Double amount = (Double)nutrient.get("amount");
        String actualUOM = ((String)nutrient.get("unitName")).toLowerCase();
        String expectedUOM = nutrientConstants.nutrientUnits.get(nutrientName);
        //get units of measurement
        //System.out.println("nutrient: "+nutrientName+" acutal: "+actualUOM + "expected: "+nutrientConstants.nutrientUnits.get(nutrientName));
        if (!actualUOM.equals(expectedUOM)) {
            //compare units, if necessary will need to figure out
            if(actualUOM.equals("mg") && expectedUOM.equals("g")) {
                System.out.println("converting mg to g for "+nutrientName);
                nutrient.put("amount", convertMGtoG(amount));
                nutrient.put("unitName", "g");
            }
        }

    }

    private static Double convertMGtoG(Double amount) {
        return amount/1000;
    }

    private static void formatCells(Sheet sheet, int initialRow, int currentRow) {
        CellRangeAddress foodLabel = new CellRangeAddress(initialRow, currentRow, 0, 0);
        CellRangeAddress foodServingSize = new CellRangeAddress(initialRow, currentRow, 1, 1);
        sheet.addMergedRegion(foodLabel);
        sheet.addMergedRegion(foodServingSize);CellStyle cs = null;
        /*CellStyle center = wb.createCellStyle();
        center.setAlignment(CellStyle.ALIGN_CENTER);
        Row row = sheet.getRow(initialRow);
        Cell cell=  row.getCell(0);
        cell.setCellStyle(center);*/

    }
}
