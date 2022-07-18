package fi.bizhop.kiekkohamsteri.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Stats extends TimestampBase {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer year;
	@Column(nullable = false)
	private Integer month;
	
	@Column(name="new_discs")
	private Integer newDiscs;
	
	@Column(name="new_users")
	private Integer newUsers;
	
	@Column(name="new_manufacturers")
	private Integer newManufacturers;
	
	@Column(name="new_plastics")
	private Integer newPlastics;
	
	@Column(name="new_molds")
	private Integer newMolds;
	
	@Column(name="sales_completed")
	private Integer salesCompleted;
	
	public Stats(Integer year, Integer month) {
		this.year = year;
		this.month = month;
	}
}
