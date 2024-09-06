package com.interview.task.githubapi.config;

import com.interview.task.githubapi.controller.GithubController;
import com.interview.task.githubapi.exception.GlobalExceptionHandler;
import com.interview.task.githubapi.exception.ResourceNotFoundException;
import com.interview.task.githubapi.exception.UnsupportedMediaTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ExceptionHandlerTest {

    private MockMvc mockMvc;

    @Mock
    private GithubController githubController;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(githubController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void shouldHandleResourceNotFoundException() throws Exception {
        //given
        String userName = "Jimmy";
        when(githubController.getUserRepositoriesMetadata(eq(userName), eq(MediaType.APPLICATION_JSON_VALUE))).thenThrow(new ResourceNotFoundException("User not found"));

        //then
        mockMvc.perform(get("/user/Jimmy/repos")
                        .header("Accept", MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    public void shouldHandleUnsupportedMediaTypeException() throws Exception {
        //given
        String userName = "Jimmy";
        when(githubController.getUserRepositoriesMetadata(eq(userName), eq(MediaType.APPLICATION_JSON_VALUE))).thenThrow(new UnsupportedMediaTypeException("Media type not acceptable"));

        //then
        mockMvc.perform(get("/user/Jimmy/repos")
                        .header("Accept", MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_ACCEPTABLE.value()))
                .andExpect(jsonPath("$.message").value("Media type not acceptable"));
    }

    @Test
    public void shouldIllegalArgumentException() throws Exception {
        //given
        String userName = "Jimmy";
        when(githubController.getUserRepositoriesMetadata(eq(userName), eq(MediaType.APPLICATION_JSON_VALUE))).thenThrow(new IllegalArgumentException("Wrong argument"));

        //then
        mockMvc.perform(get("/user/Jimmy/repos")
                        .header("Accept", MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Wrong argument"));
    }

    @Test
    public void shouldRuntimeException() throws Exception {
        //given
        String userName = "Jimmy";
        when(githubController.getUserRepositoriesMetadata(eq(userName), eq(MediaType.APPLICATION_JSON_VALUE))).thenThrow(new RuntimeException("Internal server error"));

        //then
        mockMvc.perform(get("/user/Jimmy/repos")
                        .header("Accept", MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }
}
