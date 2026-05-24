package com.financas.controller;

import com.financas.dto.HolidayCheckResponse;
import com.financas.dto.HolidayDTO;
import com.financas.service.HolidayService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/holidays")
public class HolidayController {

    private final HolidayService holidayService;

    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @GetMapping("/check")
    public ResponseEntity<HolidayCheckResponse> checkDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(holidayService.checkDate(date));
    }

    @GetMapping("/{year}")
    public ResponseEntity<List<HolidayDTO>> getHolidays(@PathVariable("year") int year) {
        return ResponseEntity.ok(holidayService.getHolidaysForYear(year));
    }
}
