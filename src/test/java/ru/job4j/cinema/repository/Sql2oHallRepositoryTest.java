package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.job4j.cinema.model.Hall;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class Sql2oHallRepositoryTest {

    @Autowired
    private Sql2oHallRepository sql2oHallRepository;

    @AfterEach
    void clearTable() {
        for (Hall hall : sql2oHallRepository.findAll()) {
            sql2oHallRepository.deleteById(hall.getId());
        }
    }

    @Test
    void whenSaveHallSuccessfullyThenGetGeneratedId() {
        Hall hall = new Hall();
        hall.setName("name");
        hall.setDescription("description");
        hall.setPlaceCount(10);
        hall.setRowCount(10);

        Optional<Hall> hallOptional = sql2oHallRepository.save(hall);

        assertThat(hallOptional).isNotEmpty();
        assertThat(hallOptional.get().getId()).isNotZero();
    }

    @Test
    void whenFindByExistingIdThenReturnTheSame() {
        Hall hall = new Hall();
        hall.setName("name");
        hall.setDescription("description");
        hall.setPlaceCount(10);
        hall.setRowCount(10);

        sql2oHallRepository.save(hall);

        Optional<Hall> foundHallOptional = sql2oHallRepository.findById(hall.getId());

        assertThat(foundHallOptional).isNotEmpty();
        assertThat(foundHallOptional.get()).usingRecursiveComparison().isEqualTo(hall);
    }

    @Test
    void whenFindHallByNonExistingIdThenReturnEmptyOptional() {
        Optional<Hall> optionalHall = sql2oHallRepository.findById(Integer.MAX_VALUE);

        assertThat(optionalHall).isEmpty();
    }

    @Test
    void whenSaveHallsThenFindAllSuccessfully() {
        Hall hall = new Hall();
        hall.setName("name");
        hall.setDescription("description");
        hall.setPlaceCount(10);
        hall.setRowCount(10);

        Hall hall2 = new Hall();
        hall2.setName("name");
        hall2.setDescription("description");
        hall2.setPlaceCount(10);
        hall2.setRowCount(10);

        sql2oHallRepository.save(hall);
        sql2oHallRepository.save(hall2);

        Collection<Hall> expected = List.of(hall, hall2);
        Collection<Hall> actual = sql2oHallRepository.findAll();

        assertThat(expected).usingRecursiveComparison().isEqualTo(actual);
    }

    @Test
    void whenDeleteHallByIdSuccessfully() {
        Hall hall = new Hall();
        hall.setName("name");
        hall.setDescription("description");
        hall.setPlaceCount(10);
        hall.setRowCount(10);

        sql2oHallRepository.save(hall);
        Collection<Hall> beforeDelete = sql2oHallRepository.findAll();
        sql2oHallRepository.deleteById(hall.getId());
        Collection<Hall> afterDelete = sql2oHallRepository.findAll();

        assertThat(beforeDelete.isEmpty()).isFalse();
        assertThat(afterDelete).isNotEqualTo(beforeDelete);
        assertThat(afterDelete.isEmpty()).isTrue();
    }
}