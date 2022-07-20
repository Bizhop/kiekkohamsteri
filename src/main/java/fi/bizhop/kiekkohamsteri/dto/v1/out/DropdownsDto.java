package fi.bizhop.kiekkohamsteri.dto.v1.out;

import java.util.List;

import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.DropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.MoldDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.PlasticDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.ManufacturerDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.ColorDropdownProjection;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class DropdownsDto {
	List<MoldDropdownProjection> molds;
	List<ManufacturerDropdownProjection> valms;
	List<PlasticDropdownProjection> muovit;
	List<ColorDropdownProjection> varit;
	List<DropdownProjection> kunto;
	List<DropdownProjection> tussit;
}
