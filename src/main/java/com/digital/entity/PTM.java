package com.digital.entity;

import com.digital.enums.PTMStatus;
import com.digital.enums.PTMType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ptm")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PTM {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ptmId;

    private String title;
    private String description;
    private LocalDateTime meetingDateTime;
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    private PTMType type;

    private String joinLink;
    private String venue;

    @Enumerated(EnumType.STRING)
    private PTMStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ptm_students",
            joinColumns = @JoinColumn(name = "ptm_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> students;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "teacher_id", nullable = false)
//    private Teacher teacher;

    /**
     * Utility method to get the list of student IDs
     */
    public List<Long> getStudentIds() {
        if (students == null || students.isEmpty()) return List.of();
        return students.stream()
                .map(Student::getStudentRegId) // ensure this matches your Student PK field
                .toList();
    }
    // Many PTMs belong to one Teacher
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", referencedColumnName = "id", nullable = false)
    private Teacher teacher;
}
