package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.File;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class Sql2oFileRepositoryTest {

    private static Sql2oFileRepository sql2oFileRepository;

    @BeforeAll
    public static void init() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oFileRepository.class.getClassLoader().getResourceAsStream("application-test.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oFileRepository = new Sql2oFileRepository(sql2o);
    }

    @AfterEach
    public void clearTable() {
        for (File file : sql2oFileRepository.findAll()) {
            sql2oFileRepository.deleteById(file.getId());
        }
    }

    @Test
    public void whenSaveFileThenGetId() {
        File file = new File("name", "path");
        Optional<File> optionalFile = sql2oFileRepository.save(file);

        assertThat(optionalFile).isNotEmpty();
        assertThat(optionalFile.get().getId()).isNotZero();
    }

    @Test
    public void whenSaveFileWithSamePathFails() {
        sql2oFileRepository.save(new File("first", "path"));
        Optional<File> secondOptional = sql2oFileRepository.save(new File("second", "path"));

        assertThat(secondOptional).isEmpty();
    }

    @Test
    public void whenFindByIdSuccessfully() {
        File file = new File("name", "path");
        sql2oFileRepository.save(file);

        Optional<File> foundOptionalFile = sql2oFileRepository.findById(file.getId());

        assertThat(foundOptionalFile).isNotEmpty();
        assertThat(file).isEqualTo(foundOptionalFile.get());
    }

    @Test
    public void whenNotFoundById() {
        Optional<File> foundOptional = sql2oFileRepository.findById(1);
        assertThat(foundOptional).isEmpty();
    }

    @Test
    public void whenDeleteByIdSuccessfully() {
        File file = new File("name", "path");
        sql2oFileRepository.save(file);

        Collection<File> firstState = sql2oFileRepository.findAll();
        sql2oFileRepository.deleteById(file.getId());
        Collection<File> secondState = sql2oFileRepository.findAll();

        assertThat(firstState.isEmpty()).isFalse();
        assertThat(secondState.isEmpty()).isTrue();
    }

    @Test
    public void whenDeleteByIdFails() {
        sql2oFileRepository.save(new File("first", "path1"));
        sql2oFileRepository.save(new File("second", "path2"));

        Collection<File> firstState = sql2oFileRepository.findAll();
        sql2oFileRepository.deleteById(10);
        Collection<File> secondState = sql2oFileRepository.findAll();

        assertThat(firstState).usingRecursiveComparison().isEqualTo(secondState);
    }

    @Test
    public void whenAllSavedFilesFoundSuccessfully() {
        File file1 = new File("file1", "path1");
        File file2 = new File("file2", "path2");
        File file3 = new File("file3", "path3");

        sql2oFileRepository.save(file1);
        sql2oFileRepository.save(file2);
        sql2oFileRepository.save(file3);

        Collection<File> expected = List.of(file1, file2, file3);
        Collection<File> actual = sql2oFileRepository.findAll();

        assertThat(expected).usingRecursiveComparison().isEqualTo(actual);
    }

}