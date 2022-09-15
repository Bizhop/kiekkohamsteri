package fi.bizhop.kiekkohamsteri.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "ostot")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Buy extends TimestampBase {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name="kiekko_id")
    Disc disc;

	@ManyToOne
	@JoinColumn(name="myyja")
	User seller;

	@ManyToOne
	@JoinColumn(name="ostaja")
	User buyer;
	
	Status status;
	
	public Buy(Disc disc, User seller, User buyer, Status status) {
		this.disc = disc;
		this.seller = seller;
		this.buyer = buyer;
		this.status = status;
	}

	public enum Status {
		REQUESTED, CONFIRMED, REJECTED;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(!(o instanceof Buy)) return false;
		var other = (Buy)o;
		return Objects.equals(this.id, other.id);
	}
}
