package com.digital.repository;

import com.digital.entity.ReportCard;
import com.digital.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportCardRepository extends JpaRepository<ReportCard, Long> {
//    List<ReportCard> findByStudentId(Long studentId);
//    //Optional<ReportCard> findByStudentIdAndTerm(Long studentId, String term);
//
//    Optional<ReportCard> findByStudentAndTerm(Student student, String term);
//    Optional<ReportCard> findByStudentStudentRegIdAndTerm(Long studentId, String term);


    //List<ReportCard> findByStudentStudentRegId(Long studentRegId);

//    Optional<ReportCard> findByStudentStudentRegIdAndTerm(Long studentRegId, String term);
//    List<ReportCard> findByStudentStudentRegIdAndTerm(Long studentRegId, String term);
Optional<ReportCard> findByStudentStudentRegIdAndTerm(Long studentRegId, String term);

    List<ReportCard> findAllByStudentStudentRegIdAndTerm(Long studentRegId, String term);

    // Optional<ReportCard> findByStudentAndTerm(Student student, String term);

    //List<ReportCard> findByStudentSchoolClassClassId(Long classId);

    //List<ReportCard> findByStudentSchoolClassClassIdAndTerm(Long classId, String term);
//    Optional<ReportCard> findByStudentStudentRegIdAndSubjectSubjectIdAndTerm(
//            Long studentRegId,
//            Long subjectId,
//            String term
//    );
    List<ReportCard> findBySubmission_SubmissionId(Long submissionId);
    List<ReportCard> findByResult_ResultId(Long resultId);
    Optional<ReportCard> findBySubmission_SubmissionIdAndSubject_SubjectId(Long submissionId, Long subjectId);
}
