package fi.bizhop.kiekkohamsteri.model;

import javax.persistence.*;

@Entity
@Table(name = "stats")
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
	
	public Stats() {}

	public Stats(Integer year, Integer month) {
		super();
		this.year = year;
		this.month = month;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getNewDiscs() {
		return newDiscs;
	}

	public void setNewDiscs(Integer newDiscs) {
		this.newDiscs = newDiscs;
	}

	public Integer getNewUsers() {
		return newUsers;
	}

	public void setNewUsers(Integer newUsers) {
		this.newUsers = newUsers;
	}

	public Integer getNewManufacturers() {
		return newManufacturers;
	}

	public void setNewManufacturers(Integer newManufacturers) {
		this.newManufacturers = newManufacturers;
	}

	public Integer getNewPlastics() {
		return newPlastics;
	}

	public void setNewPlastics(Integer newPlastics) {
		this.newPlastics = newPlastics;
	}

	public Integer getNewMolds() {
		return newMolds;
	}

	public void setNewMolds(Integer newMolds) {
		this.newMolds = newMolds;
	}

	public Integer getSalesCompleted() {
		return salesCompleted;
	}

	public void setSalesCompleted(Integer salesCompleted) {
		this.salesCompleted = salesCompleted;
	}
}
