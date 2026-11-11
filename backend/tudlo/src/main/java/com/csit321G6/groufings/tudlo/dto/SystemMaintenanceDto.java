package com.csit321G6.groufings.tudlo.dto;

import com.csit321G6.groufings.tudlo.entity.MaintenanceAction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public final class SystemMaintenanceDto {

    private SystemMaintenanceDto() {
    }

    public record Request(
            @NotNull Long adminId,
            @NotNull MaintenanceAction actionType,
            @Size(max = 500) String description
    ) {
    }

    public record Response(
            Long id,
            Long adminId,
            MaintenanceAction actionType,
            String description,
            LocalDateTime performedAt
    ) {
    }
}
