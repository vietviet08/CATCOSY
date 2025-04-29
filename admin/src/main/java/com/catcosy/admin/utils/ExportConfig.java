package com.catcosy.admin.utils;

import com.catcosy.library.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportConfig {

    private int sheetIndex;

    private int startRow;

    private Class dataClazz;

    private List<CellConfig> cellExportConfigList;


    public static final ExportConfig customerExport;
    static{
        customerExport = new ExportConfig();
        customerExport.setSheetIndex(0);
        customerExport.setStartRow(1);
        customerExport.setDataClazz(Customer.class);
        List<CellConfig> customerCellConfig = new ArrayList<>();
        customerCellConfig.add(new CellConfig(0, "id"));
        customerCellConfig.add(new CellConfig(1, "firstName"));
        customerCellConfig.add(new CellConfig(2, "lastName"));
        customerCellConfig.add(new CellConfig(3, "username"));
//        customerCellConfig.add(new CellConfig(4, "birthday"));
        customerCellConfig.add(new CellConfig(5, "sex"));
        customerCellConfig.add(new CellConfig(6, "email"));
        customerCellConfig.add(new CellConfig(7, "phone"));
        customerCellConfig.add(new CellConfig(8, "addressDetail"));
        customerCellConfig.add(new CellConfig(9, "provider"));

        customerExport.setCellExportConfigList(customerCellConfig);
    }

}