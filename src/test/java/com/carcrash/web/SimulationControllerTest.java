package com.carcrash.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SimulationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fullFlow_createFieldAddTwoCarsAndCollide() throws Exception {
        mockMvc.perform(post("/api/reset")).andExpect(status().isNoContent());

        mockMvc.perform(post("/api/field")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"width\":10,\"height\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.width").value(10))
                .andExpect(jsonPath("$.height").value(10));

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"A\",\"x\":1,\"y\":2,\"direction\":\"N\",\"commands\":\"FFRFFFFRRL\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"B\",\"x\":7,\"y\":8,\"direction\":\"W\",\"commands\":\"FFLFFFFFFF\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(post("/api/simulate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].collided").value(true))
                .andExpect(jsonPath("$[0].collisionStep").value(7))
                .andExpect(jsonPath("$[1].collided").value(true));
    }

    @Test
    void addingCarWithDuplicateNameIsRejected() throws Exception {
        mockMvc.perform(post("/api/reset"));
        mockMvc.perform(post("/api/field")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"width\":5,\"height\":5}"));
        mockMvc.perform(post("/api/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"A\",\"x\":0,\"y\":0,\"direction\":\"N\",\"commands\":\"F\"}"));

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"A\",\"x\":1,\"y\":1,\"direction\":\"N\",\"commands\":\"F\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void runningSimulationBeforeAddingACarIsRejected() throws Exception {
        mockMvc.perform(post("/api/reset"));
        mockMvc.perform(post("/api/field")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"width\":5,\"height\":5}"));

        mockMvc.perform(post("/api/simulate"))
                .andExpect(status().isConflict());
    }
}
