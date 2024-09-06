package com.interview.task.githubapi.controller;

import com.interview.task.githubapi.exception.UnsupportedMediaTypeException;
import com.interview.task.githubapi.model.domain.UserRepoMetadata;
import com.interview.task.githubapi.service.GithubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Github Controller", description = "Exposed controller for github metadata")
@RestController
@AllArgsConstructor
public class GithubController {

    private final GithubService githubService;

    @Operation(
            summary = "get user repo metadata",
            description = "get user repo which are not forks name and all branches with last sha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "resource not found"),
            @ApiResponse(responseCode = "406", description = "not supported media type"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")
    })
    @GetMapping(value = "/user/{username}/repos")
    public ResponseEntity<List<UserRepoMetadata>> getUserRepositoriesMetadata(@PathVariable("username") String userName,
                                                                              @RequestHeader("Accept") String accept) {
        validateHeader(accept);
        return ResponseEntity.ok(githubService.getUserRepoMetadata(userName));
    }

    private void validateHeader(String acceptHeader) {
        if (!acceptHeader.equals(MediaType.APPLICATION_JSON_VALUE)) {
            throw new UnsupportedMediaTypeException("Accept header with value " + acceptHeader + " is not supported");
        }
    }
}
