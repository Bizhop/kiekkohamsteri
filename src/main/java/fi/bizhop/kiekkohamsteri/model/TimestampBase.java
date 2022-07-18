package fi.bizhop.kiekkohamsteri.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

@MappedSuperclass
@Setter
public class TimestampBase {
	  @Column(name = "created_at", updatable = false)
	  @Temporal(TemporalType.TIMESTAMP)
	  @CreationTimestamp
	  private Date createdAt;

	  @Column(name = "updated_at")
	  @Temporal(TemporalType.TIMESTAMP)
	  @UpdateTimestamp
	  private Date updatedAt;

	  public TimestampBase() {}

	  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSZ", locale = "fi_FI")
	  public Date getCreatedAt() {
	    return createdAt;
	  }

	  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSZ", locale = "fi_FI")
	  public Date getUpdatedAt() {
	    return updatedAt;
	  }
}
