package fi.bizhop.kiekkohamsteri.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
public class TimestampBase {
	  @Column(name = "created_at")
	  @Temporal(TemporalType.TIMESTAMP)
	  @CreationTimestamp
	  private Date createdAt;

	  @Column(name = "updated_at")
	  @Temporal(TemporalType.TIMESTAMP)
	  @UpdateTimestamp
	  private Date updatedAt;

	  public TimestampBase() {}

	  public Date getCreatedAt() {
	    return createdAt;
	  }

	  public void setCreatedAt(Date createdAt) {
	    this.createdAt = createdAt;
	  }

	  public Date getUpdatedAt() {
	    return updatedAt;
	  }

	  public void setUpdatedAt(Date updatedAt) {
	    this.updatedAt = updatedAt;
	  }
}
