package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.common.*;
import com.csit321G6.groufings.tudlo.dto.SystemMaintenanceDto;
import com.csit321G6.groufings.tudlo.service.SystemMaintenanceService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system-maintenance")
public class SystemMaintenanceController extends CrudController<SystemMaintenanceDto.Request, SystemMaintenanceDto.Response> {

    private final SystemMaintenanceService service;

    public SystemMaintenanceController(SystemMaintenanceService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, SystemMaintenanceDto.Request, SystemMaintenanceDto.Response> service() {
        return service;
    }
}
