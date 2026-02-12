package com.phs.ewallet.controller;

import com.phs.ewallet.service.IncomeEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IncomeEmailController {

    private final IncomeEmailService incomeEmailService;

    @GetMapping("/email/income-excel")
    public ResponseEntity<String> sendIncomeEmail() {

        incomeEmailService.sendIncomeExcelToEmail();

        return ResponseEntity.ok("Income email sent successfully");
    }
}

