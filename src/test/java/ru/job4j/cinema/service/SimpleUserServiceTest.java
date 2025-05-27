package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SimpleUserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new SimpleUserService(userRepository);
    }

    @Test
    void whenSaveUserThenReturnUserWithId() {
        User user = new User("ivan@mail.com", "Ivan", "pass");
        when(userRepository.save(user)).thenReturn(Optional.of(user));

        Optional<User> result = userService.save(user);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("ivan@mail.com");
    }

    @Test
    void whenFindByEmailAndPasswordThenReturnUser() {
        User user = new User("ivan@mail.com", "Ivan", "pass");
        when(userRepository.findByEmailAndPassword("ivan@mail.com", "pass")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmailAndPassword("ivan@mail.com", "pass");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Ivan");
    }

    @Test
    void whenFindByWrongEmailAndPasswordThenReturnEmpty() {
        when(userRepository.findByEmailAndPassword("wrong@mail.com", "1234")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmailAndPassword("wrong@mail.com", "1234");

        assertThat(result).isEmpty();
    }
}
