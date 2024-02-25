package ru.ioque.testingsystem.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.testingsystem.repository.DailyValueEntityRepository;
import ru.ioque.testingsystem.repository.IntradayValueEntityRepository;

import java.util.List;
@RestController
@AllArgsConstructor
public class WebController {
    DailyValueEntityRepository dailyValueEntityRepository;
    IntradayValueEntityRepository intradayValueEntityRepository;

    @GetMapping("/api/v1/daily-values")
    public List<DailyValueResponse> getDailyValues(
        @RequestParam(defaultValue = "0") Integer pageNumber,
        @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        return dailyValueEntityRepository
            .findAll(PageRequest.of(pageNumber, pageSize))
            .stream()
            .map(DailyValueResponse::from)
            .toList();
    }

    @GetMapping("/api/v1/intraday-values")
    public List<IntradayValueResponse> getIntradayValues(
        @RequestParam(defaultValue = "0") Integer pageNumber,
        @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        return intradayValueEntityRepository
            .findAll(PageRequest.of(pageNumber, pageSize))
            .stream()
            .map(IntradayValueResponse::from)
            .toList();
    }
}
