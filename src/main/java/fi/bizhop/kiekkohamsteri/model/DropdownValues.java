package fi.bizhop.kiekkohamsteri.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "dd_arvot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DropdownValues implements Dropdown {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="valikko")
	private String menu;

	@Column(name="nimi")
	private String name;

	@Column(name="arvo")
	private Long value;
}
