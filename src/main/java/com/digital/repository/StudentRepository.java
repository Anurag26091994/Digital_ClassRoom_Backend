package com.digital.repository;

import com.digital.entity.SchoolClass;
import com.digital.entity.Student;
import com.digital.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUserUserId(Long userId);
    Optional<Student> findByRollNumber(String rollNumber);
    boolean existsByEmail(String email);

    boolean existsByRollNumberAndSchoolClass_ClassId(String rollNumber, Long classId);

    Optional<Student> findByUserUsername(String username);

    Optional<Student> findByUser_Username(String username);
    //List<Student> findAllBySchoolClass_ClassIdAndSection_SectionId(Long classId, Long sectionId);


    List<Student> findBySchoolClass_ClassId(Long classId);

   // List<Student> findBySchoolClass_ClassIdAndSection_SectionId(Long classId, Long sectionId);

   // Optional<Student> findByUser_UserId(Long userId);
    //Optional<Student> findByUserUsername(String username);
    Optional<Student> findByUser(User user);
    //List<Student> findBySchoolClassClassIdAndSectionSectionId(Long classId, Long sectionId);

    List<Student> findAllBySchoolClass_ClassIdAndSection_SectionId(Long classId, Long sectionId);
    List<Student> findBySchoolClass_ClassIdAndSection_SectionId(Long classId, Long sectionId);


//    List<Student> findAllBySchoolClass_ClassIdAndSection_SectionId(Long classId, Long sectionId);

//    List<Student> findBySchoolClass_ClassId(Long classId);

  //  List<Student> findBySchoolClass_ClassIdAndSection_SectionId(Long classId, Long sectionId);
    Optional<Student> findByUser_UserId(Long userId);
   // Optional<Student> findByUser(User user);


}
