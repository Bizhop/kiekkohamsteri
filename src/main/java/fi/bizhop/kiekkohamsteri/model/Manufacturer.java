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
public class Manufacturer extends TimestampBase implements Dropdown {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="valmistaja")
	private String name;

	@Override
	public Long getValue() {
		return this.id;
	}
}