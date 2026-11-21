package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.dto.SectionDto;
import com.csit321G6.groufings.tudlo.service.SectionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
public class SectionController {

    private final SectionService service;

    public SectionController(SectionService service) {
        this.service = service;
    }

    @GetMapping
    public List<SectionDto.Response> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public SectionDto.Response getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SectionDto.Response create(@Valid @RequestBody SectionDto.Request request) {
        return service.create(request);
    }
}
