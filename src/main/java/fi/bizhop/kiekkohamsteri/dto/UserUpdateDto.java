package fi.bizhop.kiekkohamsteri.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserUpdateDto {
	String username;
	String etunimi;
	String sukunimi;
	Integer pdga_num;
	boolean publicDiscCount;
	boolean publicList;
}
