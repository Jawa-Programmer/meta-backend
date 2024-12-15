package ru.dozen.mephi.meta;


import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import ru.dozen.mephi.meta.repository.UsersRepository;

@AutoConfigureMockMvc
public class AbstractIntegrationTest extends AbstractTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected PasswordEncoder encoder;

    @Autowired
    protected UsersRepository usersRepository;

    @AfterEach
    void clearDatabase() {
        usersRepository.deleteAll();
    }
}
