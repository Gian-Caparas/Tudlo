package com.csit321G6.groufings.tudlo.admin;

import com.csit321G6.groufings.tudlo.common.*;
import com.csit321G6.groufings.tudlo.common.exception.BadRequestException;
import com.csit321G6.groufings.tudlo.user.UserRepository;
import com.csit321G6.groufings.tudlo.user.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
@Service
public class AdminService extends CrudService<Admin, AdminDto.Request, AdminDto.Response> {

    private final AdminRepository repository;
    private final UserRepository userRepository;

    public AdminService(AdminRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    protected JpaRepository<Admin, Long> repository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "Admin";
    }

    @Override
    protected Admin newEntity() {
        return new Admin();
    }

    @Override
    protected void apply(Admin entity, AdminDto.Request request) {
        entity.setUser(find(userRepository, request.userId(), "User"));
        if (entity.getUser().getUserType() != UserType.ADMIN) {
            throw new BadRequestException("User " + entity.getUser().getId() + " is not an ADMIN");
        }
    }

    @Override
    protected AdminDto.Response toResponse(Admin entity) {
        return new AdminDto.Response(
                entity.getId(),
                entity.getUser().getId());
    }
}
