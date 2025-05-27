package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FileControllerTest {

    private FileService fileService;
    private FileController fileController;

    @BeforeEach
    void setUp() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
    }

    @Test
    void whenGetByIdThenStatusOkAndHasBody() {
        var expectedContent = new byte[]{42, 24};
        when(fileService.getFileById(5)).thenReturn(Optional.of(new FileDto("file.txt", expectedContent)));

        var response = fileController.getById(5);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedContent);
    }

    @Test
    void whenGetByIdNotFoundThenStatus404() {
        when(fileService.getFileById(5)).thenReturn(Optional.empty());

        var response = fileController.getById(5);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.hasBody()).isFalse();
    }
}
