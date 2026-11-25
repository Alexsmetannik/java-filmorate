package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User validUser() {
        return User.builder()
                .email("user@example.com")
                .login("user")
                .name("User")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }

    @Test
    void createUserReturnsUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.login").value("user"));
    }

    @Test
    void createUserWithBlankNameUsesLogin() throws Exception {
        User user = validUser();
        user.setName("");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    void createUserWithBlankEmailReturns400() throws Exception {
        User user = validUser();
        user.setEmail("");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithEmailWithoutAtReturns400() throws Exception {
        User user = validUser();
        user.setEmail("userexample.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithLoginContainingSpaceReturns400() throws Exception {
        User user = validUser();
        user.setLogin("us er");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithFutureBirthdayReturns400() throws Exception {
        User user = validUser();
        user.setBirthday(LocalDate.now().plusDays(1));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void emptyRequestBodyReturns400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllUsersReturnsList() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateNonExistingUserReturns404() throws Exception {
        User user = validUser();
        user.setId(999_999L);
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }
}
