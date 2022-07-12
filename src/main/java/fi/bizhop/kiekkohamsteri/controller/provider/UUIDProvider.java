package fi.bizhop.kiekkohamsteri.controller.provider;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDProvider {
    public String getUuid() {
        return UUID.randomUUID().toString();
    }
}
