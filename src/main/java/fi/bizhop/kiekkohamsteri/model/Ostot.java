package fi.bizhop.kiekkohamsteri.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ostot")
public class Ostot extends TimestampBase {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="kiekko_id")
	Kiekot kiekko;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="myyja")
	Members myyja;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="ostaja")
	Members ostaja;
	
	Status status;
	
	public Ostot() {
		super();
	}
	
	public Ostot(Kiekot kiekko, Members myyja, Members ostaja, Status status) {
		super();
		this.kiekko = kiekko;
		this.myyja = myyja;
		this.ostaja = ostaja;
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
}
