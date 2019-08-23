package com.johndoe.workoutbuddy.infrastructure.database.user;

import com.johndoe.workoutbuddy.domain.user.dto.GenderDto;
import com.johndoe.workoutbuddy.domain.user.dto.UserDto;
import com.johndoe.workoutbuddy.domain.user.port.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
@Profile("inmemory")
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, UserDto> users = new HashMap<>();

    public InMemoryUserRepository() {
        users.put("admin", UserDto.builder()
                .username("admin")
                .password("pass")
                .active(true)
                .roles(Set.of("ADMIN")).build());

        users.put("user", UserDto.builder()
                .username("user")
                .password("pass")
                .active(false)
                .roles(Set.of("USER"))
                .firstName("John")
                .lastName("Doe")
                .gender(GenderDto.MALE)
                .weight(90d)
                .height(183d)
                .build());
    }

    @Override
    public Optional<UserDto> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    @Override
    public Optional<UserDto> findByEmail(String email) {
        return users.values().stream().filter(user -> email.equals(user.getEmail())).findFirst();
    }

    @Override
    public String saveUser(UserDto user) {
        users.put(user.getUsername(), user);
        return user.getUsername();
    }
}
