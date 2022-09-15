package fi.bizhop.kiekkohamsteri.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "r_vari")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Color extends TimestampBase {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="vari")
	private String name;
}
