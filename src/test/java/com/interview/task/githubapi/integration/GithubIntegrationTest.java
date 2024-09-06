package com.interview.task.githubapi.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.interview.task.githubapi.exception.ResourceNotFoundException;
import com.interview.task.githubapi.exception.UnsupportedMediaTypeException;
import com.interview.task.githubapi.model.domain.UserRepoMetadata;
import com.interview.task.githubapi.model.dto.GithubBranchResponse;
import com.interview.task.githubapi.model.dto.GithubRepoResponse;
import com.interview.task.githubapi.service.GithubService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.jsonResponse;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GithubIntegrationTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private GithubService githubService;

    @Value("${integration.rest.api.github.apiVersionHeader}")
    String apiVersionHeader;

    @Value("${integration.rest.api.github.authHeader}")
    String authToken;

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(10870));
        wireMockServer.start();
    }

    @AfterAll
    public static void setUp() {
        wireMockServer.shutdown();
    }

    @Test
    public void shouldReturnProperResponseWhenNoForks() throws JsonProcessingException {
        //given
        String userName = "testUserName";
        String repoName = "testRepo";
        String branchName = "testBranchName";
        String sha = UUID.randomUUID().toString();
        String userUrl = "/users/" + userName;
        String repoUrl = "/users/" + userName + "/repos";
        String branchUrl = "/repos/" + userName + "/" + repoName + "/branches";
        GithubRepoResponse githubRepoResponse = new GithubRepoResponse(false, repoName, new GithubRepoResponse.Owner(userName));
        GithubBranchResponse githubBranchResponse = new GithubBranchResponse(branchName, new GithubBranchResponse.Commit(sha));
        stubUserRequest(userUrl, OK.value());
        stubUserRepoRequest(repoUrl, githubRepoResponse, OK.value());
        stubUserBranchRequest(branchUrl, githubBranchResponse, OK.value());

        //when
        List<UserRepoMetadata> userRepoMetadata = githubService.getUserRepoMetadata(userName);

        //then
        Assertions.assertThat(userRepoMetadata).hasSize(1);
        UserRepoMetadata userRepo = userRepoMetadata.get(0);
        Assertions.assertThat(userRepo.name()).isEqualTo(repoName);
        Assertions.assertThat(userRepo.login()).isEqualTo(userName);
        Assertions.assertThat(userRepo.branches()).hasSize(1);
        Assertions.assertThat(userRepo.branches()).extracting("name")
                .containsExactlyInAnyOrder(branchName);
        Assertions.assertThat(userRepo.branches()).extracting("sha")
                .containsExactlyInAnyOrder(sha);
    }

    @Test
    public void shouldReturnProperResponseWhenExistingForks() throws JsonProcessingException {
        //given
        String userName = "testUserName";
        String repoName = "testRepo";
        String userUrl = "/users/" + userName;
        String repoUrl = "/users/" + userName + "/repos";
        GithubRepoResponse githubRepoResponse = new GithubRepoResponse(true, repoName, new GithubRepoResponse.Owner(userName));
        stubUserRequest(userUrl, OK.value());
        stubUserRepoRequest(repoUrl, githubRepoResponse, OK.value());

        //when
        List<UserRepoMetadata> userRepoMetadata = githubService.getUserRepoMetadata(userName);

        //then
        Assertions.assertThat(userRepoMetadata).isEmpty();
    }

    @Test
    public void shouldThrownResourceNotFoundExceptionWhenUSerNotExist() {
        //given
        String userName = "testUserName";
        String userUrl = "/users/" + userName;
        stubUserRequest(userUrl, NOT_FOUND.value());

        //when
        assertThatThrownBy(() -> githubService.getUserRepoMetadata(userName)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void shouldThrownUnsupportedMediaTypeExceptionWhenClientReturn415() {
        //given
        String userName = "testUserName";
        String userUrl = "/users/" + userName;
        stubUserRequest(userUrl, UNSUPPORTED_MEDIA_TYPE.value());

        //when
        assertThatThrownBy(() -> githubService.getUserRepoMetadata(userName)).isInstanceOf(UnsupportedMediaTypeException.class);
    }

    private void stubUserRequest(String url, int status) {
        wireMockServer.stubFor(get(url)
                .willReturn(aResponse()
                        .withStatus(status)));
    }

    private void stubUserRepoRequest(String url, GithubRepoResponse githubRepoResponse, int status) throws JsonProcessingException {
        wireMockServer.stubFor(get(url)
                .withHeader("X-GitHub-Api-Version", containing(apiVersionHeader))
                .withHeader("Authorization", containing("Bearer " + authToken))
                .willReturn(jsonResponse(objectMapper.writeValueAsString(List.of(githubRepoResponse)), status)));
    }

    private void stubUserBranchRequest(String url, GithubBranchResponse githubBranchResponse, int status) throws JsonProcessingException {
        wireMockServer.stubFor(get(url)
                .withHeader("X-GitHub-Api-Version", containing(apiVersionHeader))
                .withHeader("Authorization", containing("Bearer " + authToken))
                .willReturn(jsonResponse(objectMapper.writeValueAsString(List.of(githubBranchResponse)), status)));
    }
}