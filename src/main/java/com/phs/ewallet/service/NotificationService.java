package com.phs.ewallet.service;

import com.phs.ewallet.dto.ExpenseDTO;
import com.phs.ewallet.entity.Profile;
import com.phs.ewallet.repository.ProfileRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    //chỉ hoạt động khi deploy thực tế
    //gửi email thông báo tới người dùng
    //sử dụng zooho mail, nghèo quá ko có tiền mua để test

    private final ProfileRepo profileRepo;
    private final EmailService emailService;
    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @Value("${eWallet.frontend.url}")
    private String frontendUrl;

    // Chạy mỗi ngày lúc 21:00 (giờ Việt Nam)
    @Scheduled(cron = "0 0 21 * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendDailyIncomeExpenseReminder() {

        log.info("Sending Daily Income Expense Reminder");

        List<Profile> profiles = profileRepo.findAll();

        for (Profile p : profiles) {
            String body =
                    "Hi " + p.getFullName() + ",<br><br>"
                            + "This is a friendly reminder to add your income and expenses for today in eWallet."
                            + "<br><br>"
                            + "<a href=\"" + frontendUrl + "\" "
                            + "style=\"display:inline-block;padding:10px 20px;"
                            + "background-color:#4CAF50;color:white;"
                            + "text-decoration:none;border-radius:5px;\">"
                            + "Open eWallet</a>"
                            + "<br><br>"
                            + "Best regards,<br>"
                            + "eWallet Team";

            emailService.sendEmail(
                    p.getEmail(),
                    "Daily Reminder: Add your incomes and expenses",
                    body
            );
        }

        log.info("Completed Daily Income Expense Reminder");
    }

    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendDailyExpenseReminder() {

        log.info("Sending Daily Expense Summary");

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        List<Profile> profiles = profileRepo.findAll();

        for (Profile p : profiles) {

            List<ExpenseDTO> todayExpenses =
                    expenseService.getExpensesForProfileOnDate(p.getId(), today);

            // Nếu hôm nay không có chi tiêu → không gửi summary
            if (todayExpenses.isEmpty()) {
                continue;
            }

            StringBuilder table = new StringBuilder();

            table.append("<table style='border-collapse:collapse;width:100%;'>");

            // Header
            table.append("<tr style='background-color:#f2f2f2;'>")
                    .append("<th style='border:1px solid #ddd;padding:8px;'>S.No</th>")
                    .append("<th style='border:1px solid #ddd;padding:8px;'>Name</th>")
                    .append("<th style='border:1px solid #ddd;padding:8px;'>Amount</th>")
                    .append("<th style='border:1px solid #ddd;padding:8px;'>Category</th>")
                    .append("</tr>");

            int i = 1;
            for (ExpenseDTO expense : todayExpenses) {
                table.append("<tr>")
                        .append("<td style='border:1px solid #ddd;padding:8px;'>").append(i++).append("</td>")
                        .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getName()).append("</td>")
                        .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getAmount()).append("</td>")
                        .append("<td style='border:1px solid #ddd;padding:8px;'>")
                        .append(expense.getCategoryName() != null ? expense.getCategoryName() : "N/A")
                        .append("</td>")
                        .append("</tr>");
            }

            table.append("</table>");

            String body =
                    "Hi " + p.getFullName() + ",<br><br>"
                            + "Here is a summary of your expenses for today:<br><br>"
                            + table
                            + "<br><br>"
                            + "<a href=\"" + frontendUrl + "\" "
                            + "style=\"display:inline-block;padding:10px 20px;"
                            + "background-color:#2196F3;color:white;"
                            + "text-decoration:none;border-radius:5px;\">"
                            + "View in eWallet</a>"
                            + "<br><br>"
                            + "Best regards,<br>"
                            + "eWallet Team";

            emailService.sendEmail(
                    p.getEmail(),
                    "Your Daily Expense Summary",
                    body
            );
        }

        log.info("Completed Daily Expense Summary");
    }
}
