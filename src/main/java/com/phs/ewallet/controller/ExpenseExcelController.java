package com.phs.ewallet.controller;

import com.phs.ewallet.service.ExpenseExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
public class ExpenseExcelController {

    private final ExpenseExcelService expenseExcelService;

    @GetMapping("/download/expenses")
    public ResponseEntity<byte[]> downloadExpenseExcel() {

        byte[] excelBytes = expenseExcelService.downloadExpenseExcel();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=expense_details.xlsx"
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }
}
