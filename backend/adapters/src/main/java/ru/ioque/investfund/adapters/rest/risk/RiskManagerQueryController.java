package ru.ioque.investfund.adapters.rest.risk;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.persistence.repositories.JpaTelegramChatRepository;
import ru.ioque.investfund.adapters.query.PsqlEmulatedPositionQueryService;
import ru.ioque.investfund.adapters.rest.risk.response.TelegramChatResponse;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "RiskManagerQueryController", description = "Контроллер запросов к модулю \"RISK MANAGER\"")
public class RiskManagerQueryController {
    JpaTelegramChatRepository chatRepository;
    PsqlEmulatedPositionQueryService emulatedPositionQueryService;

    @GetMapping("/api/telegram-chat")
    public List<TelegramChatResponse> getTelegramChats() {
        return chatRepository.findAll().stream().map(TelegramChatResponse::from).toList();
    }

//    @GetMapping("/api/emulated-position")
//    public List<EmulatedPositionResponse> getAllEmulatedPositions(
//        @RequestParam(required = false) String ticker,
//        @RequestParam(required = false) UUID scannerId,
//        @RequestParam(defaultValue = "0") Integer pageNumber,
//        @RequestParam(defaultValue = "100") Integer pageSize,
//        @RequestParam(defaultValue = "ASC") String orderValue,
//        @RequestParam(defaultValue = "instrument.ticker") String orderField
//    ) {
//        return emulatedPositionQueryService
//            .findEmulatedPositions(
//                EmulatedPositionFilterParams.builder()
//                    .ticker(ticker)
//                    .scannerId(scannerId)
//                    .pageNumber(pageNumber)
//                    .pageSize(pageSize)
//                    .orderDirection(orderValue)
//                    .orderField(orderField)
//                    .build()
//            )
//            .stream()
//            .map(EmulatedPositionResponse::from)
//            .toList();
//    }
}
