package fi.bizhop.kiekkohamsteri;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BaseAdder {
    private final String component;
    private final Type type;

    public String create(String filename) {
        return String.format("expected/%s/%s/%s", type.name().toLowerCase(), component, filename);
    }

    public enum Type {
        CONTROLLER, REPOSITORY;
    }
}
