package fi.bizhop.kiekkohamsteri.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "group_requests")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class GroupRequest extends TimestampBase {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name="source_user_id")
    private User source;

    @ManyToOne
    @JoinColumn(name="target_user_id")
    private User target;

    private Type type;

    private Status status;

    private String info;

    public GroupRequest(Group group, User source, User target, Type type, Status status, String info) {
        this.group = group;
        this.source = source;
        this.target = target;
        this.type = type;
        this.status = status;
        this.info = info;
    }

    public enum Type {
        JOIN, KICK, PROMOTE, DEMOTE
    }

    public enum Status {
        REQUESTED, COMPLETED, REJECTED
    }
}
