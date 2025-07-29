package backend.testorder.model;

import java.time.LocalDateTime;

import backend.healthrecord.model.HealthRecord;
import backend.testtype.model.TestType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "test_order")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String unit;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String name;
    
    @Column(columnDefinition = "NVARCHAR(100)")
    private String result;
    
    @Column(columnDefinition = "NVARCHAR(100)")
    private String paymentStatus;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String note;    

    private LocalDateTime expectedResultTime;
    
    private LocalDateTime actualResultTime;
    
    @ManyToOne
    @JoinColumn(name = "healthRecordId", referencedColumnName = "id")
    private HealthRecord healthRecord;

    @OneToOne
    @JoinColumn(name = "testTypeId", referencedColumnName = "id")
    private TestType type;
}
