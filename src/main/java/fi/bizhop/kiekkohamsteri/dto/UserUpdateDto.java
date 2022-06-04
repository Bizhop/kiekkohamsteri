package fi.bizhop.kiekkohamsteri.dto;

import lombok.Data;

@Data
public class UserUpdateDto {
	private String username;
	private String etunimi;
	private String sukunimi;
	private Integer pdga_num;
	private boolean publicDiscCount;
	private boolean publicList;
}
