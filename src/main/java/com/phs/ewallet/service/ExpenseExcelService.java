package com.phs.ewallet.service;

import com.phs.ewallet.entity.Expense;
import com.phs.ewallet.repository.ExpenseRepo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseExcelService {

    private final ExpenseRepo expenseRepo;

    /**
     * Controller chỉ gọi hàm này
     */
    public byte[] downloadExpenseExcel() {

        // 1️⃣ Lấy data từ DB
        List<Expense> expenses = expenseRepo.findAllWithCategory();

        // 2️⃣ Export Excel
        return exportExpenseToExcel(expenses);
    }

    /**
     * Logic export excel
     */
    private byte[] exportExpenseToExcel(List<Expense> expenses) {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Expenses");

            // ===== HEADER =====
            Row header = sheet.createRow(0);
            String[] columns = { "S.No", "Name", "Category", "Amount", "Date" };

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // ===== DATA =====
            int rowIdx = 1;
            int serialNo = 1;

            for (Expense expense : expenses) {

                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(serialNo++);
                row.createCell(1).setCellValue(expense.getName());

                row.createCell(2).setCellValue(
                        expense.getCategory() != null
                                ? expense.getCategory().getName()
                                : ""
                );

                row.createCell(3).setCellValue(
                        expense.getAmount() != null
                                ? expense.getAmount().doubleValue()
                                : 0
                );

                row.createCell(4).setCellValue(
                        expense.getDate() != null
                                ? expense.getDate().toString()
                                : ""
                );
            }

            // Auto size column
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export expense excel", e);
        }
    }
}
