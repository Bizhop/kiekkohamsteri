package fi.bizhop.kiekkohamsteri.util;

import fi.bizhop.kiekkohamsteri.db.DiscRepository;
import fi.bizhop.kiekkohamsteri.model.Disc;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Profile("migrations")
@Component
@RequiredArgsConstructor
public class Migrations {
    protected static final Logger LOG = LogManager.getLogger(Migrations.class);
    private final DiscRepository discRepo;

    @EventListener(ApplicationReadyEvent.class)
    public void runDiscUuidMigration() {
        LOG.debug("running disc uuid migration...");

        var discs = discRepo.findAll();
        var modifiedDiscs = discs.stream()
                .filter(disc -> disc.getUuid() == null)
                .peek(Disc::generateAndSetUuid)
                .peek(Migrations::logNewUuid)
                .collect(Collectors.toList());

        discRepo.saveAll(modifiedDiscs);

        LOG.debug("...disc uuid migration done");
    }

    private static void logNewUuid(Disc disc) {
        LOG.debug("New uuid generated for disc id={}, uuid={}", disc.getId(), disc.getUuid());
    }
}
