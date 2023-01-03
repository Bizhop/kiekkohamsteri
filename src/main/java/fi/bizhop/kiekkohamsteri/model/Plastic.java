package fi.bizhop.kiekkohamsteri.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "r_muovi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Plastic extends TimestampBase implements Dropdown {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="valmistaja_id")
	private Manufacturer manufacturer;

	@Column(name="muovi")
	private String name;

	@Override
	public Long getValue() {
		return this.id;
	}
}