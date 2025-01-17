package ru.dozen.mephi.meta;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ResourceUtils;

@AutoConfigureWireMock(port = 0)
@ContextConfiguration(initializers = TestContainersInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AbstractTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ResourceLoader resourceLoader;

    @SneakyThrows
    protected <T> T readResourceValue(String resourcePath, Class<T> tClass) {
        Resource resource = resourceLoader.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + resourcePath);
        return objectMapper.readValue(resource.getInputStream(), tClass);
    }

    @SneakyThrows
    protected <T> T readResourceValue(String resourcePath, TypeReference<T> valueTypeRef) {
        Resource resource = resourceLoader.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + resourcePath);
        return objectMapper.readValue(resource.getInputStream(), valueTypeRef);
    }
}
