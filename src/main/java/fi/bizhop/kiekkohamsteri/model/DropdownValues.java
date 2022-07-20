package fi.bizhop.kiekkohamsteri.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "dd_arvot")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DropdownValues {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="valikko")
	private String menu;

	@Column(name="nimi")
	private String name;

	@Column(name="arvo")
	private Integer value;
}
