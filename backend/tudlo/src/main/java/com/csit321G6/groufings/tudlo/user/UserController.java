package com.csit321G6.groufings.tudlo.user;

import com.csit321G6.groufings.tudlo.common.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController extends CrudController<UserDto.Request, UserDto.Response> {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, UserDto.Request, UserDto.Response> service() {
        return service;
    }
}
