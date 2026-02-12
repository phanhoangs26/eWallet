package com.phs.ewallet.controller;

import com.phs.ewallet.service.ExpenseEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExpenseEmailController {

    private final ExpenseEmailService expenseEmailService;

    @GetMapping("/email/expense-excel")
    public ResponseEntity<String> sendExpenseEmail() {

        expenseEmailService.sendExpenseExcelToEmail();

        return ResponseEntity.ok("Expense email sent successfully");
    }
}

