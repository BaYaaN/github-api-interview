package com.interview.task.githubapi.client;

import com.interview.task.githubapi.config.FeignConfig;
import com.interview.task.githubapi.model.dto.GithubBranchResponse;
import com.interview.task.githubapi.model.dto.GithubRepoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(
        name = "github-client",
        url = "${integration.rest.api.github.url}",
        configuration = FeignConfig.class
)
public interface GithubClient {

    @GetMapping(value = "/repos/{owner}/{repo}/branches")
    List<GithubBranchResponse> getBranchesByUserAndRepo(@PathVariable("owner") String userName,
                                                        @PathVariable("repo") String repo,
                                                        @RequestHeader HttpHeaders headers);

    @GetMapping(value = "/users/{username}/repos")
    List<GithubRepoResponse> getReposByUser(@PathVariable("username") String userName,
                                            @RequestHeader HttpHeaders headers);

    @GetMapping(value = "/users/{username}")
    ResponseEntity getUser(@PathVariable("username") String userName,
                           @RequestHeader HttpHeaders headers);
}
