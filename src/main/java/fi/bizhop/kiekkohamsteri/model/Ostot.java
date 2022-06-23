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
public class Ostot extends TimestampBase {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name="kiekko_id")
	Kiekot kiekko;

	@ManyToOne
	@JoinColumn(name="myyja")
	Members myyja;

	@ManyToOne
	@JoinColumn(name="ostaja")
	Members ostaja;
	
	Status status;
	
	public Ostot(Kiekot disc, Members seller, Members buyer, Status status) {
		this.kiekko = disc;
		this.myyja = seller;
		this.ostaja = buyer;
		this.status = status;
	}

	public enum Status {
		REQUESTED, CONFIRMED, REJECTED;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(!(o instanceof Ostot)) return false;
		var other = (Ostot)o;
		return Objects.equals(this.id, other.id);
	}
}
