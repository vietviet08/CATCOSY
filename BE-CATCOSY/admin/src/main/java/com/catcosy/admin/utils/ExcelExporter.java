package com.catcosy.admin.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import com.catcosy.library.enums.ObjectManage;
import com.catcosy.library.model.Category;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class ExcelExporter<T> {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private static String exportFor = "";
    private List<T> objects;
    private List<String> fieldNames;

    public ExcelExporter(List<T> objects) {
        this.objects = objects;
        workbook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet(exportFor);

        if (exportFor.equals(ObjectManage.Categories.name())) {
            writeHeaderCategories();
        } else if (exportFor.equals(ObjectManage.Products.name())) {
            writeHeaderForProducts();
        } else if (exportFor.equals(ObjectManage.Orders.name())) {
            writeHeaderOrders();
        } else if (exportFor.equals(ObjectManage.Vouchers.name())) {
            writeHeaderVouchers();
        } else if (exportFor.equals(ObjectManage.Employees.name())) {
            writeHeaderEmployees();
        } else if (exportFor.equals(ObjectManage.Customers.name())) {
            writeHeaderForCustomers();
        }

    }

    private CellStyle setStyleForHeader() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Arial");
        font.setFontHeight(12);
        font.setColor(IndexedColors.WHITE.getIndex());


        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.index);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);

        return style;
    }

    private void writeHeaderForProducts() {
        Row row = sheet.createRow(0);

        CellStyle style = setStyleForHeader();

        createCell(row, 0, "ID", style);
        createCell(row, 1, "Product Name", style);
        createCell(row, 2, "Name Category", style);
        createCell(row, 3, "Cost Price", style);
        createCell(row, 4, "Sale Price", style);
        createCell(row, 5, "Description", style);
        createCell(row, 6, "Activate", style);

        writeData();
    }

    private void writeHeaderCategories() {
        Row row = sheet.createRow(0);

        CellStyle style = setStyleForHeader();

        createCell(row, 0, "ID", style);
        createCell(row, 1, "Category Name", style);
        createCell(row, 2, "Activated", style);
        createCell(row, 3, "Deleted", style);

        writeData();
    }

    private void writeHeaderVouchers() {
        Row row = sheet.createRow(0);

        CellStyle style = setStyleForHeader();

        createCell(row, 0, "ID", style);
        createCell(row, 1, "Code", style);
        createCell(row, 2, "Price", style);
        createCell(row, 3, "Percent Of Total Price", style);
        createCell(row, 4, "Minimum Price", style);
        createCell(row, 5, "Minimum Total Price", style);
        createCell(row, 6, "Usage Limits", style);
        createCell(row, 7, "Expiry Date", style);
        createCell(row, 8, "For Email Customer", style);
        createCell(row, 9, "Used", style);
        createCell(row, 10, "Activate", style);

        writeData();
    }

    private void writeHeaderEmployees() {
        Row row = sheet.createRow(0);

        CellStyle style = setStyleForHeader();

        createCell(row, 0, "ID", style);
        createCell(row, 1, "First Name", style);
        createCell(row, 2, "Last Name", style);
        createCell(row, 3, "Username", style);
        createCell(row, 4, "Email", style);
        createCell(row, 5, "Phone", style);
        createCell(row, 6, "Enable", style);

        writeData();
    }

    private void writeHeaderOrders() {
        Row row = sheet.createRow(0);

        CellStyle style = setStyleForHeader();

        createCell(row, 0, "ID", style);
        createCell(row, 1, "Order Date", style);
        createCell(row, 2, "Delivery Date", style);
        createCell(row, 3, "Total Price", style);
        createCell(row, 4, "Discount Price", style);
        createCell(row, 5, "Shipping Fee", style);
        createCell(row, 6, "Delivery Address", style);
        createCell(row, 7, "Payment Method", style);
        createCell(row, 8, "Status", style);
        createCell(row, 9, "Notes", style);
        createCell(row, 10, "Accept", style);
        createCell(row, 11, "Cancel", style);

        writeData();
    }

    private void writeHeaderForCustomers() {
        Row row = sheet.createRow(0);

        CellStyle style = setStyleForHeader();
        createCell(row, 0, "ID", style);
        createCell(row, 1, "First Name", style);
        createCell(row, 2, "Last Name", style);
        createCell(row, 3, "Sex", style);
        createCell(row, 4, "Address Detail", style);
        createCell(row, 5, "E-mail", style);
        createCell(row, 6, "Phone", style);
        createCell(row, 7, "Provider", style);
        createCell(row, 8, "Activate", style);

        writeData();
    }


    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        }
        else if (value instanceof Category) {
            cell.setCellValue(((Category) value).getName());
        }else if (value instanceof Date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            cell.setCellValue(dateFormat.format((Date) value));
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }


    private CellStyle setStyleForCell() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(10);
        font.setFontName("Arial");
        font.setBold(false);


        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setFont(font);

        return style;
    }

    private void writeData() {
        int rowCount = 1;

        CellStyle style = setStyleForCell();
        T firstObject = objects.get(0);
        Field[] fields = firstObject.getClass().getDeclaredFields();

        for (T obj : objects) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            for (String fieldName : fieldNames) {
                try {
                    Field field = getField(obj.getClass(), fieldName);
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    createCell(row, columnCount++, value, style);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    private Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }


//

    public void export(HttpServletResponse response, String exportFor, List<String> fieldNames) throws IOException {
        this.exportFor = exportFor;
        this.fieldNames = fieldNames;

        writeHeaderLine();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
}