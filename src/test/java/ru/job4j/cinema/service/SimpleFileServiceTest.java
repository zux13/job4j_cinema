package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleFileServiceTest {

    private FileService fileService;

    private FileRepository fileRepository;

    @BeforeEach
    public void initFields() {
        fileRepository = mock(FileRepository.class);
        fileService = new SimpleFileService(fileRepository);
    }

    @Test
    void whenFileExistsThenReturnFileDto() throws IOException {
        Path tempFile = Files.createTempFile("test-file", ".txt");
        byte[] expectedContent = "test content".getBytes();
        Files.write(tempFile, expectedContent);

        File file = new File("test-file.txt", tempFile.toAbsolutePath().toString());

        when(fileRepository.findById(1)).thenReturn(Optional.of(file));

        Optional<FileDto> result = fileService.getFileById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("test-file.txt");
        assertThat(result.get().getContent()).isEqualTo(expectedContent);

        Files.deleteIfExists(tempFile);
    }

    @Test
    void whenFileNotFoundThenReturnEmptyOptional() {
        when(fileRepository.findById(99)).thenReturn(Optional.empty());

        Optional<FileDto> result = fileService.getFileById(99);

        assertThat(result).isEmpty();
    }

}