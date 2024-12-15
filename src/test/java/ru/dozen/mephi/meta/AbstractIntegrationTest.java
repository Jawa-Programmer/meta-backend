package ru.dozen.mephi.meta;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public class AbstractIntegrationTest extends AbstractTest {

    @Autowired
    protected MockMvc mockMvc;
}
