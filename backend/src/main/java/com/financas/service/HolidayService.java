package com.financas.service;

import com.financas.client.HolidayApiClient;
import com.financas.dto.HolidayCheckResponse;
import com.financas.dto.HolidayDTO;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HolidayService {

    private final HolidayApiClient holidayApiClient;
    
    // Cache em memoria
    private final Map<Integer, List<HolidayDTO>> holidaysCache = new ConcurrentHashMap<>();

    public HolidayService(HolidayApiClient holidayApiClient) {
        this.holidayApiClient = holidayApiClient;
    }

    public List<HolidayDTO> getHolidaysForYear(int year) {
        return holidaysCache.computeIfAbsent(year, holidayApiClient::getHolidaysByYear);
    }

    public HolidayCheckResponse checkDate(LocalDate date) {
        HolidayCheckResponse response = new HolidayCheckResponse();
        
        // Verifica se é final de semana
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            response.setWeekend(true);
        } else {
            response.setWeekend(false);
        }

        // Verifica se é feriado
        List<HolidayDTO> holidays = getHolidaysForYear(date.getYear());
        Optional<HolidayDTO> holidayOpt = holidays.stream()
                .filter(h -> h.getDate().equals(date))
                .findFirst();

        if (holidayOpt.isPresent()) {
            response.setHoliday(true);
            response.setHolidayName(holidayOpt.get().getName());
        } else {
            response.setHoliday(false);
        }

        return response;
    }
}
