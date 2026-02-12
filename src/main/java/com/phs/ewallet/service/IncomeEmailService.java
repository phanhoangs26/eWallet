package com.phs.ewallet.service;

import com.phs.ewallet.entity.Income;
import com.phs.ewallet.entity.Profile;
import com.phs.ewallet.repository.IncomeRepo;
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
public class IncomeEmailService {

    private final IncomeRepo incomeRepo;
    private final ProfileService profileService;
    private final JavaMailSender mailSender;

    public void sendIncomeExcelToEmail() {

        try {
            // 1️⃣ Lấy profile hiện tại
            Profile profile = profileService.getCurrentProfile();

            // 2️⃣ Lấy income theo profile
            List<Income> incomes =
                    incomeRepo.findAllWithCategoryByProfileId(profile.getId());

            // 3️⃣ Tạo file excel
            byte[] excelFile = exportIncomeToExcel(incomes);

            // 4️⃣ Gửi mail
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(profile.getEmail());
            helper.setSubject("Your Income Report");
            helper.setText("Please find attached your income report.");

            helper.addAttachment(
                    "income_details.xlsx",
                    () -> new java.io.ByteArrayInputStream(excelFile)
            );

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send income email", e);
        }
    }

    private byte[] exportIncomeToExcel(List<Income> incomes) {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Income");

            Row header = sheet.createRow(0);
            String[] columns = { "S.No", "Name", "Category", "Amount", "Date" };

            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

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

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate income excel", e);
        }
    }
}
