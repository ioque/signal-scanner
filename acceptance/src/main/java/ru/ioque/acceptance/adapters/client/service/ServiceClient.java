package ru.ioque.acceptance.adapters.client.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.acceptance.adapters.client.RestTemplateFacade;

@Component
@AllArgsConstructor
public class ServiceClient {
    RestTemplateFacade restTemplateFacade;

    public void clearState() {
        restTemplateFacade.delete("/api/v1/service/state");
    }
}
