package fi.bizhop.kiekkohamsteri.util;

import fi.bizhop.kiekkohamsteri.db.DiscRepository;
import fi.bizhop.kiekkohamsteri.model.Disc;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Profile("migrations")
@Component
@RequiredArgsConstructor
public class Migrations {
    private final DiscRepository discRepo;

    @EventListener(ApplicationReadyEvent.class)
    public void runDiscUuidMigration() {
        System.out.println("running disc uuid migration...");

        var discs = discRepo.findAll();
        var modifiedDiscs = discs.stream()
                .filter(disc -> disc.getUuid() == null)
                .peek(Disc::generateAndSetUuid)
                .collect(Collectors.toList());

        discRepo.saveAll(modifiedDiscs);
    }
}
