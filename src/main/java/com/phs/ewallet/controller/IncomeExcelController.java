package com.phs.ewallet.controller;

import com.phs.ewallet.service.IncomeExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
public class IncomeExcelController {

    private final IncomeExcelService incomeExcelService;

    @GetMapping("/download/incomes")
    public ResponseEntity<byte[]> downloadIncomeExcel() {

        byte[] excelBytes = incomeExcelService.downloadIncomeExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=income_details.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }
}
