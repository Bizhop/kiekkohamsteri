package fi.bizhop.kiekkohamsteri.model;

import fi.bizhop.kiekkohamsteri.util.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "kiekot")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Disc extends TimestampBase {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="member_id")
	private User owner;

	@ManyToOne
	@JoinColumn(name="mold_id")
	private Mold mold;

	@ManyToOne
	@JoinColumn(name="muovi_id")
	private Plastic plastic;

	@ManyToOne
	@JoinColumn(name="vari_id")
	private Color color;

	@Column(name="kuva")
	private String image;

	@Column(name="paino")
	private Integer weight;

	@Column(name="kunto", nullable = false)
	private Integer condition;

	@Column(name="hohto")
	private Boolean glow;

	@Column(name="spessu")
	private Boolean special;

	@Column(nullable = false)
	private Boolean dyed;

	@Column(nullable = false)
	private Boolean swirly;

	@Column(name="tussit", nullable = false)
	private Integer markings;

	@Column(name="myynnissa")
	private Boolean forSale;

	@Column(name="hinta", nullable = false)
	private Integer price;

	@Column(name="muuta")
	private String description;

	@Column(name="loytokiekko")
	private Boolean lostAndFound;

	@Column(nullable = false)
	private Boolean itb;
	
	@Column(name="public", nullable = false)
	private Boolean publicDisc;

	@Column(nullable = false)
	private Boolean lost;

	@Column(length = 36)
	private String uuid;
	
	public Disc(User user, Mold defaultMold, Plastic defaultPlastic, Color defaultColor) {
		this.owner = user;
		this.mold = defaultMold;
		this.plastic = defaultPlastic;
		this.color = defaultColor;
		this.image = "";
		this.weight = 175;
		this.condition = 10;
		this.glow = false;
		this.special = false;
		this.dyed = false;
		this.swirly = false;
		this.markings = 1;
		this.forSale = false;
		this.price = 0;
		this.description = "";
		this.lostAndFound = false;
		this.itb = false;
		this.publicDisc = false;
		this.lost = false;
		this.uuid = Utils.generateUuid();
	}

	public void generateAndSetUuid() {
		var newUuid = Utils.generateUuid(String.format("%s-%d", owner.getEmail(), this.id));
		this.uuid = newUuid;
	}
}
