package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.ColorRepository;
import fi.bizhop.kiekkohamsteri.model.Color;
import fi.bizhop.kiekkohamsteri.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ColorService {
    private static final Long DEFAULT_COLOR_ID = Utils.getLongFromEnv("DEFAULT_COLOR_ID", 1L);

    final ColorRepository colorRepo;

    private Color DEFAULT_COLOR;

    public Color getDefaultColor() {
        if(DEFAULT_COLOR == null) {
            DEFAULT_COLOR = colorRepo.findById(DEFAULT_COLOR_ID).orElseThrow();
        }
        return DEFAULT_COLOR;
    }

    // Passthrough methods to db
    // Not covered (or to be covered by unit tests)

    public Color getColor(Long id) {
        return colorRepo.findById(id).orElse(null);
    }
}
