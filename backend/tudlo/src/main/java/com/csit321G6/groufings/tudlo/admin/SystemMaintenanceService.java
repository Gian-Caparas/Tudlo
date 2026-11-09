package com.csit321G6.groufings.tudlo.admin;

import com.csit321G6.groufings.tudlo.common.CrudService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class SystemMaintenanceService
        extends CrudService<SystemMaintenance, SystemMaintenanceDto.Request, SystemMaintenanceDto.Response> {

    private final SystemMaintenanceRepository repository;
    private final AdminRepository adminRepository;

    public SystemMaintenanceService(
            SystemMaintenanceRepository repository,
            AdminRepository adminRepository) {
        this.repository = repository;
        this.adminRepository = adminRepository;
    }

    @Override
    protected JpaRepository<SystemMaintenance, Long> repository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "SystemMaintenance";
    }

    @Override
    protected SystemMaintenance newEntity() {
        return new SystemMaintenance();
    }

    @Override
    protected void apply(SystemMaintenance entity, SystemMaintenanceDto.Request request) {
        entity.setAdmin(find(adminRepository, request.adminId(), "Admin"));
        entity.setActionType(request.actionType());
        entity.setDescription(request.description());
    }

    @Override
    protected SystemMaintenanceDto.Response toResponse(SystemMaintenance entity) {
        return new SystemMaintenanceDto.Response(
                entity.getId(),
                entity.getAdmin().getId(),
                entity.getActionType(),
                entity.getDescription(),
                entity.getPerformedAt());
    }
}
