package com.phs.ewallet.service;

import com.phs.ewallet.entity.Expense;
import com.phs.ewallet.entity.Profile;
import com.phs.ewallet.repository.ExpenseRepo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseEmailService {

    private final ExpenseRepo expenseRepo;
    private final ProfileService profileService;
    private final JavaMailSender mailSender;

    public void sendExpenseExcelToEmail() {

        try {
            // 1️⃣ Lấy profile hiện tại
            Profile profile = profileService.getCurrentProfile();

            // 2️⃣ Lấy expense theo profile
            List<Expense> expenses =
                    expenseRepo.findAllWithCategoryByProfileId(profile.getId());

            // 3️⃣ Tạo file excel
            byte[] excelFile = exportExpenseToExcel(expenses);

            // 4️⃣ Gửi email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(profile.getEmail());
            helper.setSubject("Your Expense Report");
            helper.setText("Please find attached your expense report.");

            helper.addAttachment(
                    "expense_details.xlsx",
                    () -> new java.io.ByteArrayInputStream(excelFile)
            );

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send expense email", e);
        }
    }

    // ===== Logic export giống download nhưng private =====
    private byte[] exportExpenseToExcel(List<Expense> expenses) {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Expenses");

            Row header = sheet.createRow(0);
            String[] columns = { "S.No", "Name", "Category", "Amount", "Date" };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);

                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

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

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate expense excel", e);
        }
    }
}
