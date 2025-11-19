package com.digital.serviceimpl;

import com.digital.dto.FeeStructureDTO;
import com.digital.entity.FeeStructure;
import com.digital.repository.FeeStructureRepository;
import com.digital.servicei.FeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeeServiceImpl implements FeeService {

    private final FeeStructureRepository repo;

    public FeeServiceImpl(FeeStructureRepository repo) {
        this.repo = repo;
    }

    @Override
    public FeeStructureDTO createFeeStructure(FeeStructureDTO dto) {
        FeeStructure entity = toEntity(dto);
        FeeStructure saved = repo.save(entity);
        return toDTO(saved);
    }

    @Override
    public FeeStructureDTO getFeeStructureByClass(Long classId, String academicYear) {
        FeeStructure fs = repo.findByClassIdAndAcademicYear(classId, academicYear)
                .orElseThrow(() -> new RuntimeException(
                        "Fee structure not found for classId=" + classId + ", year=" + academicYear));
        return toDTO(fs);
    }

    @Override
    public List<FeeStructureDTO> getAllFeeStructures() {
        return repo.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FeeStructureDTO updateFeeStructure(Long id, FeeStructureDTO dto) {
        FeeStructure existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee structure not found with id=" + id));

        existing.setClassId(dto.getClassId());
        existing.setAcademicYear(dto.getAcademicYear());
        existing.setTuitionFee(dto.getTuitionFee());
        existing.setExamFee(dto.getExamFee());
        existing.setTransportFee(dto.getTransportFee());
        existing.setLibraryFee(dto.getLibraryFee());
        existing.setOtherCharges(dto.getOtherCharges());

        FeeStructure updated = repo.save(existing);
        return toDTO(updated);
    }

    @Override
    public void deleteFeeStructure(Long id) {
        FeeStructure existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee structure not found with id=" + id));
        repo.delete(existing);
    }

    @Override
    public List<FeeStructureDTO> getByClassAndYear(Long classId, String year) {
        return repo.findByClassIdAndAcademicYear(classId, year)
                .map(this::toDTO)
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<FeeStructure> findByClassId(Long classId) {
        return repo.findByClassId(classId);
    }

    @Override
    public List<FeeStructureDTO> getByClass(Long classId) {
        return repo.findByClassId(classId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    private FeeStructure toEntity(FeeStructureDTO dto) {
        FeeStructure fs = new FeeStructure();
        fs.setFeeId(dto.getFeeId());
        fs.setClassId(dto.getClassId());
        fs.setAcademicYear(dto.getAcademicYear());
        fs.setTuitionFee(dto.getTuitionFee());
        fs.setExamFee(dto.getExamFee());
        fs.setTransportFee(dto.getTransportFee());
        fs.setLibraryFee(dto.getLibraryFee());
        fs.setOtherCharges(dto.getOtherCharges());
        return fs;
    }

    private FeeStructureDTO toDTO(FeeStructure entity) {
        FeeStructureDTO dto = new FeeStructureDTO();
        dto.setFeeId(entity.getFeeId());
        dto.setClassId(entity.getClassId());
        dto.setAcademicYear(entity.getAcademicYear());
        dto.setTuitionFee(entity.getTuitionFee());
        dto.setExamFee(entity.getExamFee());
        dto.setTransportFee(entity.getTransportFee());
        dto.setLibraryFee(entity.getLibraryFee());
        dto.setOtherCharges(entity.getOtherCharges());
        dto.setTotalAmount(entity.getTotalAmount());
        return dto;
    }
}
