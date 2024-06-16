package com.dacs1.admin;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class AdminApplicationTests {

    @Test
    void contextLoads() {
        UUID uuid = UUID.randomUUID() ;
        String code = uuid.toString();
        System.out.println(code);

    }

}
