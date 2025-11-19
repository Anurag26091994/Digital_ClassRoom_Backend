package com.digital.entity;

import com.digital.enums.Relationship;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parent_student_mappings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"parent_id", "student_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentStudentMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_id")
    private Long mappingId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;


    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", length = 20)
    private Relationship relationshipType; // FATHER/MOTHER/GUARDIAN
}
