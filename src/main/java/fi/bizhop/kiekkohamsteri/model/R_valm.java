package fi.bizhop.kiekkohamsteri.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "r_valm")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class R_valm extends TimestampBase {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String valmistaja;
}