package com.interview.task.githubapi.service;

import com.interview.task.githubapi.client.GithubClient;
import com.interview.task.githubapi.mapper.GithubResponseMapper;
import com.interview.task.githubapi.model.domain.UserRepoMetadata;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GithubService {
    private static final String API_VERSION_HEADER = "X-GitHub-Api-Version";
    private final GithubClient githubClient;
    private final GithubResponseMapper githubResponseMapper;
    private final String apiVersion;
    private final String token;

    public GithubService(GithubClient githubClient,
                         GithubResponseMapper githubResponseMapper,
                         @Value("${integration.rest.api.github.apiVersionHeader}") String apiVersion,
                         @Value("${integration.rest.api.github.authHeader}") String token) {
        this.githubClient = githubClient;
        this.githubResponseMapper = githubResponseMapper;
        this.apiVersion = apiVersion;
        this.token = token;
    }

    public List<UserRepoMetadata> getUserRepoMetadata(@NonNull String userName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(API_VERSION_HEADER, apiVersion);
        httpHeaders.setBearerAuth(token);

        if (githubClient.getUser(userName, httpHeaders).getStatusCode().is2xxSuccessful()) {
            log.debug("User with login: {} is valid", userName);
        }

        return githubClient.getReposByUser(userName, httpHeaders)
                .stream()
                .filter(response -> !response.fork())
                .map(response -> githubResponseMapper.mapToRepoData(response,
                        githubClient.getBranchesByUserAndRepo(response.owner().login(), response.name(), httpHeaders)))
                .toList();
    }
}
