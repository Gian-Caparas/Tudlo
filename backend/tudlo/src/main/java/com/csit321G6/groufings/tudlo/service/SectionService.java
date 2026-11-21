package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.exception.BadRequestException;
import com.csit321G6.groufings.tudlo.common.exception.ConflictException;
import com.csit321G6.groufings.tudlo.common.exception.ResourceNotFoundException;
import com.csit321G6.groufings.tudlo.dto.SectionDto;
import com.csit321G6.groufings.tudlo.entity.Section;
import com.csit321G6.groufings.tudlo.repository.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Transactional(readOnly = true)
    public List<SectionDto.Response> findAll() {
        return sectionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SectionDto.Response findById(Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section", id));
        return toResponse(section);
    }

    @Transactional
    public SectionDto.Response create(SectionDto.Request request) {
        String normalized = request.sectionName().trim();
        if (sectionRepository.existsBySectionNameIgnoreCase(normalized)) {
            throw new ConflictException("Section already exists: " + normalized);
        }
        Section section = new Section();
        section.setSectionName(normalized);
        return toResponse(sectionRepository.saveAndFlush(section));
    }

    @Transactional
    public Section findOrCreate(String sectionName) {
        String normalized = sectionName != null ? sectionName.trim() : "";
        if (normalized.isEmpty()) {
            throw new BadRequestException("Class section is required");
        }
        return sectionRepository.findBySectionNameIgnoreCase(normalized)
                .orElseGet(() -> {
                    Section section = new Section();
                    section.setSectionName(normalized);
                    return sectionRepository.saveAndFlush(section);
                });
    }

    private SectionDto.Response toResponse(Section entity) {
        return new SectionDto.Response(entity.getId(), entity.getSectionName());
    }
}
