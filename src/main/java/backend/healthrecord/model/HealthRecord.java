package backend.healthrecord.model;

import backend.regimen.model.Regimen;
import backend.schedule.model.Schedule;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "health_record")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String hivStatus;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String bloodType;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String treatmentStatus;

    private float weight;

    private float height;

    @Nullable
    private float testOrderPrice;

    @ManyToOne
    @JoinColumn(name = "scheduleId", referencedColumnName = "id")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "regimenId", referencedColumnName = "id")
    private Regimen regimen;
}