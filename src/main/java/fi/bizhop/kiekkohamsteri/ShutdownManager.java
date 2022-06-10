package fi.bizhop.kiekkohamsteri;

import org.springframework.stereotype.Component;

@Component
public class ShutdownManager {
    public void shutdown() { Application.exit(); }
}
