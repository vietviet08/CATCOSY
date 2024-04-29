package com.dacs1.library;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.InputStream;

@SpringBootTest
class LibraryApplicationTests {

    @Test
    void contextLoads() {
        try {
            InputStream imageStream = getClass().getResourceAsStream("/static/images/logo.png");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
