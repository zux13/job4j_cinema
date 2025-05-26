package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.job4j.cinema.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class Sql2oUserRepositoryTest {

    @Autowired
    private Sql2oUserRepository sql2oUserRepository;

    @AfterEach
    void clearTable() {
        for (User user : sql2oUserRepository.findAll()) {
            sql2oUserRepository.deleteById(user.getId());
        }
    }

    @Test
    void whenSaveUserSuccessfullyThenGetGeneratedId() {
        User user = new User("email@email.ru", "Ivan", "Password");
        Optional<User> optionalUser = sql2oUserRepository.save(user);

        assertThat(optionalUser).isNotEmpty();
        assertThat(optionalUser.orElseThrow().getId()).isNotZero();
    }

    @Test
    void whenSaveUserWithSameEmailAndGetEmptyOptional() {
        User user = new User("email@email.ru", "Ivan", "Password");
        User user1 = new User("email@email.ru", "Ivan", "Password");

        Optional<User> optionalUser = sql2oUserRepository.save(user);
        Optional<User> optionalUser1 = sql2oUserRepository.save(user1);

        assertThat(optionalUser).isNotEmpty();
        assertThat(optionalUser1).isEmpty();
    }

    @Test
    void whenFindUserByExistingEmailAndPasswordThenReturnTheSame() {
        String email = "email@email.ru";
        String password = "Password";

        User user = new User(email, "Ivan", password);
        sql2oUserRepository.save(user);

        Optional<User> foundOptional = sql2oUserRepository.findByEmailAndPassword(email, password);

        assertThat(foundOptional).isNotEmpty();
        assertThat(foundOptional.orElseThrow()).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void whenFindUserByNotExistingEmailAndPasswordThenGetEmptyOptional() {
        Optional<User> optionalUser = sql2oUserRepository.findByEmailAndPassword("not@exist.ru", "pass");

        assertThat(optionalUser).isEmpty();
    }

    @Test
    void whenSaveUsersThenFindAllSuccessfully() {
        User user = new User("email@mail.ru", "Ivan", "Password");
        User user1 = new User("email1@mail.ru", "Oleg", "Password");

        sql2oUserRepository.save(user);
        sql2oUserRepository.save(user1);

        Collection<User> expected = List.of(user, user1);
        Collection<User> actual = sql2oUserRepository.findAll();

        assertThat(expected).usingRecursiveComparison().isEqualTo(actual);
    }

    @Test
    void whenDeleteUserByIdSuccessfully() {
        User user1 = new User("email@mail.ru", "Ivan", "Password");
        User user2 = new User("alyona@mail.ru", "Alyona", "Password");

        sql2oUserRepository.save(user1);
        sql2oUserRepository.save(user2);

        sql2oUserRepository.deleteById(user1.getId());

        Collection<User> expected = List.of(user2);
        Collection<User> actual = sql2oUserRepository.findAll();

        assertThat(expected).usingRecursiveComparison().isEqualTo(actual);
    }

    @Test
    void whenDeleteUserByNotExistingIdThenDoNothing() {
        User user1 = new User("email@mail.ru", "Ivan", "Password");
        User user2 = new User("alyona@mail.ru", "Alyona", "Password");

        sql2oUserRepository.save(user1);
        sql2oUserRepository.save(user2);

        sql2oUserRepository.deleteById(Integer.MAX_VALUE);

        Collection<User> expected = List.of(user1, user2);
        Collection<User> actual = sql2oUserRepository.findAll();

        assertThat(expected).usingRecursiveComparison().isEqualTo(actual);
    }

}