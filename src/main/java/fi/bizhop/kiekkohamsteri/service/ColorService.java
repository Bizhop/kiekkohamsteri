package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.ColorRepository;
import fi.bizhop.kiekkohamsteri.model.R_vari;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ColorService {
    final ColorRepository colorRepo;

    private R_vari DEFAULT_COLOR;

    public Optional<R_vari> getColor(Long id) {
        return colorRepo.findById(id);
    }

    public R_vari getDefaultColor() {
        if(DEFAULT_COLOR == null) {
            DEFAULT_COLOR = colorRepo.findById(1L).orElseThrow();
        }
        return DEFAULT_COLOR;
    }
}
