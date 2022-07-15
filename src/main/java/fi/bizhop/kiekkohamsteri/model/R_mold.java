package fi.bizhop.kiekkohamsteri.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "r_mold")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class R_mold extends TimestampBase {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="valmistaja_id")
	private R_valm valmistaja;
		
	private String kiekko;
	private Double nopeus;
	private Double liito;
	private Double vakaus;
	private Double feidi;
}
