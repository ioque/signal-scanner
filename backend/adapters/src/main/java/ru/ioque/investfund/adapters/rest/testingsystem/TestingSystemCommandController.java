package ru.ioque.investfund.adapters.rest.testingsystem;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Tag(name = "TestingSystemCommandController", description = "Контроллер команд к модулю \"Система тестирования\"")
public class TestingSystemCommandController {

}
