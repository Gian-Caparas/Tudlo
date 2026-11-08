package com.csit321G6.groufings.tudlo.admin;

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
