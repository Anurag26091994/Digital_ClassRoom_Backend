package com.digital.serviceimpl;

import com.digital.entity.SchoolClass;
import com.digital.exception.DuplicateResourceException;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.ClassRepository;
import com.digital.servicei.ClassService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;

    public ClassServiceImpl(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    @Override
    public SchoolClass createClass(SchoolClass schoolClass) {
        if (classRepository.existsByClassName(schoolClass.getClassName())) {
            throw new DuplicateResourceException("Class with name '" + schoolClass.getClassName() + "' already exists!");
        }
        return classRepository.save(schoolClass);
    }

    @Override
    public SchoolClass updateClass(Long classId, SchoolClass schoolClass) {
        SchoolClass existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID: " + classId));

        if (classRepository.existsByClassName(schoolClass.getClassName()) &&
                !existingClass.getClassName().equals(schoolClass.getClassName())) {
            throw new DuplicateResourceException("Class with name '" + schoolClass.getClassName() + "' already exists!");
        }

        existingClass.setClassName(schoolClass.getClassName());
        existingClass.setDescription(schoolClass.getDescription());

        return classRepository.save(existingClass);
    }

    @Override
    public void deleteClass(Long classId) {
        if (!classRepository.existsById(classId)) {
            throw new ResourceNotFoundException("Class not found with ID: " + classId);
        }
        classRepository.deleteById(classId);
    }

    @Override
    public SchoolClass getClassById(Long classId) {
        return classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID: " + classId));
    }

    @Override
    public List<SchoolClass> getAllClasses() {
        return classRepository.findAll();
    }

}
