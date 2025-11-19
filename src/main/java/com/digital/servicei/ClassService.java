package com.digital.servicei;

import com.digital.entity.SchoolClass;
import org.springframework.http.HttpStatusCode;

import java.util.List;

public interface    ClassService {
    SchoolClass createClass(SchoolClass schoolClass);
    SchoolClass updateClass(Long classId, SchoolClass schoolClass);
    void deleteClass(Long classId);
   SchoolClass getClassById(Long classId);
    List<SchoolClass> getAllClasses();
}
