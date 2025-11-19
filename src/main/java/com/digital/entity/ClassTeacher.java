package com.digital.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "class_teacher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"schoolClass", "section"})
public class ClassTeacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classTeacherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classId")
    private SchoolClass schoolClass;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sectionId")
    private Section section;
}


