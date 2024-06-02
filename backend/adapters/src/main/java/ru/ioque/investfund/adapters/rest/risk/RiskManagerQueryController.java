package ru.ioque.investfund.adapters.rest.risk;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.psql.dao.JpaTelegramChatRepository;
import ru.ioque.investfund.adapters.rest.risk.response.TelegramChatResponse;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "RiskManagerQueryController", description = "Контроллер запросов к модулю \"RISK MANAGER\"")
public class RiskManagerQueryController {
    JpaTelegramChatRepository chatRepository;

    @GetMapping("/api/telegram-chat")
    public List<TelegramChatResponse> getTelegramChats() {
        return chatRepository.findAll().stream().map(TelegramChatResponse::from).toList();
    }
}
