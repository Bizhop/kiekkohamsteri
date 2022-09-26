package fi.bizhop.kiekkohamsteri.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User extends TimestampBase {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String username;
	private String email;

	@Column(name="etunimi")
	private String firstName;

	@Column(name="sukunimi")
	private String lastName;

	@Column(name="pdga_num")
	private Integer pdgaNumber;

	@Transient
	private String jwt; //do not persist

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_groups",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "group_id")
	)
	private Set<Group> groups;
	
	public User(String userEmail) {
		this.username = "New User";
		this.email = userEmail;
		this.firstName = "New";
		this.lastName = "User";
		this.pdgaNumber = 0;
		this.roles = new HashSet<>();
		this.groups = new HashSet<>();
	}

	@Override
	public boolean equals(Object other) {
		if(other == null) return false;
		if(!(other instanceof User)) return false;

		var otherUser = (User)other;
		if(this.email == null) return false; //users with null emails are not equal
		return this.email.equals(otherUser.email);
	}
}