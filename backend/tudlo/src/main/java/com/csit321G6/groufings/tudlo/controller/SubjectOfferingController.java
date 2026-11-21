package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.dto.SubjectOfferingDto;
import com.csit321G6.groufings.tudlo.service.SubjectOfferingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offerings")
public class SubjectOfferingController {

    private final SubjectOfferingService service;

    public SubjectOfferingController(SubjectOfferingService service) {
        this.service = service;
    }

    @GetMapping
    public List<SubjectOfferingDto.Response> getAll() {
        return service.findAllForCurrentUser();
    }

    @GetMapping("/{offeringId}")
    public SubjectOfferingDto.Response getById(@PathVariable Long offeringId) {
        return service.findById(offeringId);
    }

    @GetMapping("/teacher/{teacherId}")
    public List<SubjectOfferingDto.Response> getByTeacherId(@PathVariable Long teacherId) {
        return service.findByTeacherId(teacherId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectOfferingDto.Response create(@Valid @RequestBody SubjectOfferingDto.Request request) {
        return service.create(request);
    }

    @DeleteMapping("/{offeringId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long offeringId) {
        service.delete(offeringId);
    }
}
