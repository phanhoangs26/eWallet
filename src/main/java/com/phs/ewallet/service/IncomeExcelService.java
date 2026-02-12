package com.phs.ewallet.service;

import com.phs.ewallet.entity.Income;
import com.phs.ewallet.repository.IncomeRepo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeExcelService {

    private final IncomeRepo incomeRepo;

    public byte[] downloadIncomeExcel() {
        // 1️⃣ Lấy data từ DB
        List<Income> incomes = incomeRepo.findAllWithCategory();

        // 2️⃣ Export Excel
        return exportIncomeToExcel(incomes);
    }

    private byte[] exportIncomeToExcel(List<Income> incomes) {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Income");

            // ===== HEADER =====
            Row header = sheet.createRow(0);
            String[] columns = { "S.No", "Name", "Category", "Amount", "Date" };

            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            // ===== DATA =====
            int rowIdx = 1;
            int serialNo = 1;

            for (Income income : incomes) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(serialNo++);
                row.createCell(1).setCellValue(income.getName());

                row.createCell(2).setCellValue(
                        income.getCategory() != null
                                ? income.getCategory().getName()
                                : ""
                );

                row.createCell(3).setCellValue(
                        income.getAmount() != null
                                ? income.getAmount().doubleValue()
                                : 0
                );

                row.createCell(4).setCellValue(
                        income.getDate() != null
                                ? income.getDate().toString()
                                : ""
                );
            }

            // Auto size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export income excel", e);
        }
    }
}
