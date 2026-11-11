package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.common.*;
import com.csit321G6.groufings.tudlo.dto.AdminDto;
import com.csit321G6.groufings.tudlo.service.AdminService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admins")
public class AdminController extends CrudController<AdminDto.Request, AdminDto.Response> {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, AdminDto.Request, AdminDto.Response> service() {
        return service;
    }
}
