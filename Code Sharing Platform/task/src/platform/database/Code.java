package platform.database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "code")
public class Code {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Column(name = "code")
    private String code;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date = new Date();

    @Column(name = "time")
    private Long time = 0L;

    @Column(name = "views")
    private Long views = 0L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timer")
    private Date timerDate = new Date();

    @Transient
    private String formattedDate = getFormattedDate();

    public String formatDate() {
        JsonObject object = new Gson().toJsonTree(this).getAsJsonObject();
        return object.getAsJsonPrimitive("date").getAsString();
    }
}
