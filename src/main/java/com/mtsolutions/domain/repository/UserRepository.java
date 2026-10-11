package com.mtsolutions.domain.repository;

import com.mtsolutions.domain.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, UUID> {

    public Optional<User> findByEmailOptional(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<User> findByEmailVerificationToken(String token) {
        return find("emailVerificationToken", token).firstResultOptional();
    }

    public List<User> findPage(int page, int size) {
        return findAll(Sort.descending("createdAt")).page(page, size).list();
    }

    public boolean existsByEmailOrCpfOrPhoneOrRg(String email, String cpf, String phone, String rg) {
        return count("email = ?1 or cpf = ?2 or phone = ?3 or rg = ?4", email, cpf, phone, rg) > 0;
    }
}
