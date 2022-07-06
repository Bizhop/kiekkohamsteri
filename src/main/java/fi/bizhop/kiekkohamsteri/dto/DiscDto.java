package fi.bizhop.kiekkohamsteri.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class DiscDto {
	Long moldId;
	Long muoviId;
	Long variId;
	String kuva;
	Integer paino;
	Integer kunto;
	Boolean hohto;
	Boolean spessu;
	Boolean dyed;
	Boolean swirly;
	Integer tussit;
	Boolean myynnissa;
	Integer hinta;
	String muuta;
	Boolean loytokiekko;
	Boolean itb;
	Boolean publicDisc;
	Boolean lost;
}
