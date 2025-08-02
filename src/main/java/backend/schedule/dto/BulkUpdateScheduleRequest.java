package backend.schedule.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class BulkUpdateScheduleRequest {
    private Long doctorId;
    private LocalDate date;
    private String roomCode;
    private LocalTime slot;   
}
