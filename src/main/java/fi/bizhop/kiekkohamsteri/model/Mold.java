package fi.bizhop.kiekkohamsteri.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "r_mold")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Mold extends TimestampBase implements Dropdown {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="valmistaja_id")
	private Manufacturer manufacturer;

	@Column(name="kiekko")
	private String name;

	@Column(name="nopeus")
	private Double speed;

	@Column(name="liito")
	private Double glide;

	@Column(name="vakaus")
	private Double stability;

	@Column(name="feidi")
	private Double fade;

	@Override
	public Long getValue() {
		return this.id;
	}
}
