package com.example.weyland.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenValidCriticalCommand_thenExecutedImmediately() throws Exception {
        String json = """
                {
                    "description": "Проверить энерго",
                    "priority": "CRITICAL",
                    "author": "Рипли",
                    "time": "2025-07-20T15:00:00Z"
                }
                """;

        mockMvc.perform(post("/commands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ACCEPTED")));
    }

    @Test
    void whenInvalidCommand_thenValidationError() throws Exception {
        String json = """
                {
                    "description": "",
                    "priority": "UNKNOWN",
                    "author": "",
                    "time": "not-a-date"
                }
                """;

        mockMvc.perform(post("/commands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }


}
