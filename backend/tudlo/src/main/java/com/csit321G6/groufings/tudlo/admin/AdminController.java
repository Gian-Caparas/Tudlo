package com.csit321G6.groufings.tudlo.admin;

import com.csit321G6.groufings.tudlo.common.*;
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
