package fi.bizhop.kiekkohamsteri.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserUpdateDto {
	String username;
	String etunimi;
	String sukunimi;
	Integer pdga_num;
	boolean publicDiscCount;
	boolean publicList;
	Integer level;
}
