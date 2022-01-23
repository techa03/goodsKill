package com.goodskill.web;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Timeout(value = 60L)
@Disabled
class MockMvcExampleTests {
    /**
     * 秒杀请求
     */
    private final String content = "{\n" +
            "\t\"requestCount\": 20,\n" +
            "\t\"seckillCount\": 10,\n" +
            "\t\"seckillId\": " + seckillId + "\n" +
            "}";

    /**
     * 秒杀id
     */
    private static final String seckillId = "1000";

    @Test
    void doWithSychronized(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(post("/sychronized")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());
        Thread.sleep(10000);
    }

    @Test
    void doWithRedissionLock(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(post("/redisson")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());
        Thread.sleep(10000);
    }

    @Test
    void doWithKafkaMqMessage(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(post("/kafka")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());
        Thread.sleep(10000);
    }

    @Test
    void doWithProcedure(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(post("/procedure")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());
        Thread.sleep(10000);
    }

    @Test
    void doWithZookeeperLock(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(post("/zookeeperLock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());
        Thread.sleep(10000);
    }

    @Test
    void redisReactiveMongo(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(post("/redisReactiveMongo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());
        Thread.sleep(10000);
    }

    @Test
    void doWithRabbitmq(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(post("/rabbitmq")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());
        Thread.sleep(10000);
    }

    @Test
    void limit(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(post("/limit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());
        Thread.sleep(10000);
    }

}