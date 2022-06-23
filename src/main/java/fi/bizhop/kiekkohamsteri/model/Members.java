package fi.bizhop.kiekkohamsteri.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Members extends TimestampBase {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String username;
	private String email;
	private Integer level;
	private String etunimi;
	private String sukunimi;
	private Integer pdga_num;
	@Transient
	private String jwt; //do not persist
	
	@Column(name="public_list", nullable = false)
	private Boolean publicList;
	
	@Column(name="public_disc_count", nullable = false)
	private Boolean publicDiscCount;
	
	@Column(name="disc_count")
	private Integer discCount;
	
	public Members(String userEmail) {
		this.username = "Uusi käyttäjä";
		this.email = userEmail;
		this.level = 1;
		this.etunimi = "Uusi";
		this.sukunimi = "Käyttäjä";
		this.pdga_num = 0;
		this.publicList = false;
		this.publicDiscCount = false;
		this.discCount = 0;
	}

	public void addDisc() {
		this.discCount++;
	}
	
	public void removeDisc() {
		this.discCount--;
	}

	@Override
	public boolean equals(Object other) {
		if(other == null) return false;
		if(!(other instanceof Members)) return false;

		var otherUser = (Members)other;
		if(this.email == null) return false; //users with null emails are not equal
		return this.email.equals(otherUser.email);
	}
}