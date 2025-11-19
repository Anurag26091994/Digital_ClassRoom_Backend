package com.digital.repository;

import com.digital.entity.PTM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PTMRepository extends JpaRepository <PTM,Long >{
    @Query("""
        SELECT DISTINCT p
        FROM PTM p
        JOIN p.students s
        JOIN ParentStudentMapping pm ON pm.student = s
        WHERE pm.parent.parentId = :parentId
        ORDER BY p.meetingDateTime DESC
    """)
    List<PTM> findPTMsForParent(@Param("parentId") Long parentId);
    List<PTM> findByStudents_StudentRegId(Long studentRegId);

}
