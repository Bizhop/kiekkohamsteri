package fi.bizhop.kiekkohamsteri.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "ostot")
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
	
	public Ostot() {
		super();
	}
	
	public Ostot(Kiekot disc, Members seller, Members buyer, Status status) {
		super();
		this.kiekko = disc;
		this.myyja = seller;
		this.ostaja = buyer;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Kiekot getKiekko() {
		return kiekko;
	}

	public void setKiekko(Kiekot kiekko) {
		this.kiekko = kiekko;
	}

	public Members getMyyja() {
		return myyja;
	}

	public void setMyyja(Members myyja) {
		this.myyja = myyja;
	}

	public Members getOstaja() {
		return ostaja;
	}

	public void setOstaja(Members ostaja) {
		this.ostaja = ostaja;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
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
