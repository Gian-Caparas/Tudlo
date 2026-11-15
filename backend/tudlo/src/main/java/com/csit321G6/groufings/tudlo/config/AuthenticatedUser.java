package com.csit321G6.groufings.tudlo.config;

import com.csit321G6.groufings.tudlo.entity.UserType;

public record AuthenticatedUser(Long userId, String email, UserType role, Long profileId) {
}
