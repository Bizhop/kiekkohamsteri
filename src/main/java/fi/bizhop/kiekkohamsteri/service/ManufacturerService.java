package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.ManufacturerRepository;
import fi.bizhop.kiekkohamsteri.model.Manufacturer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManufacturerService {
    final ManufacturerRepository manufacturerRepository;

    // Passthrough methods to db
    // Not covered (or to be covered by unit tests)

    public Optional<Manufacturer> getManufacturer(Long manufacturerId) {
        return manufacturerRepository.findById(manufacturerId);
    }
}
