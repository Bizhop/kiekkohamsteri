package fi.bizhop.kiekkohamsteri.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "r_valm")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Manufacturer extends TimestampBase {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="valmistaja")
	private String name;
}