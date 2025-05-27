package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.HallRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SimpleHallServiceTest {

    private HallRepository hallRepository;
    private HallService hallService;

    @BeforeEach
    void setUp() {
        hallRepository = mock(HallRepository.class);
        hallService = new SimpleHallService(hallRepository);
    }

    @Test
    void whenFindByIdThenReturnHall() {
        Hall hall = new Hall();
        hall.setId(1);
        hall.setName("Hall A");
        hall.setDescription("Scheme A");
        hall.setRowCount(10);
        hall.setPlaceCount(10);

        when(hallRepository.findById(1)).thenReturn(Optional.of(hall));

        Optional<Hall> result = hallService.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Hall A");
    }

    @Test
    void whenFindAllThenReturnListOfHalls() {
        Hall hallA = new Hall();
        hallA.setId(1);
        hallA.setName("Hall A");
        hallA.setDescription("Scheme A");
        hallA.setRowCount(10);
        hallA.setPlaceCount(10);

        Hall hallB = new Hall();
        hallB.setId(2);
        hallB.setName("Hall B");
        hallB.setDescription("Scheme B");
        hallB.setRowCount(10);
        hallB.setPlaceCount(10);

        List<Hall> halls = List.of(hallA, hallB);
        when(hallRepository.findAll()).thenReturn(halls);

        var result = hallService.findAll();

        assertThat(result).hasSize(2).containsExactlyElementsOf(halls);
    }

    @Test
    void whenGetHallsMapThenReturnMapWithIds() {
        Hall hallA = new Hall();
        hallA.setId(1);
        hallA.setName("Hall A");
        hallA.setDescription("Scheme A");
        hallA.setRowCount(10);
        hallA.setPlaceCount(10);

        Hall hallB = new Hall();
        hallB.setId(2);
        hallB.setName("Hall B");
        hallB.setDescription("Scheme B");
        hallB.setRowCount(10);
        hallB.setPlaceCount(10);

        List<Hall> halls = List.of(hallA, hallB);
        when(hallRepository.findAll()).thenReturn(halls);

        Map<Integer, Hall> result = hallService.getHallsMap();

        assertThat(result).hasSize(2);
        assertThat(result.get(1).getName()).isEqualTo("Hall A");
        assertThat(result.get(2).getName()).isEqualTo("Hall B");
    }
}
