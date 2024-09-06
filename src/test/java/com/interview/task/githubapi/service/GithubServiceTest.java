package com.interview.task.githubapi.service;

import com.interview.task.githubapi.client.GithubClient;
import com.interview.task.githubapi.mapper.GithubResponseMapper;
import com.interview.task.githubapi.model.domain.UserRepoMetadata;
import com.interview.task.githubapi.model.dto.GithubBranchResponse;
import com.interview.task.githubapi.model.dto.GithubRepoResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GithubServiceTest {

    private final GithubClient githubClient = mock(GithubClient.class);
    private final GithubResponseMapper githubResponseMapper = new GithubResponseMapper();
    private GithubService githubService;

    @BeforeEach
    public void init() {
        githubService = new GithubService(githubClient, githubResponseMapper, "apiVersion", "token");
    }

    @Test
    public void shouldReturnOnlyReposWhichAreNotForks() {
        //given
        String userName = "test";
        String repoName1 = "TestRepo1";
        String repoName2 = "TestRepo2";
        String commitSha = UUID.randomUUID().toString();
        String commitSha2 = UUID.randomUUID().toString();
        String branchName = "develop";
        String branchName2 = "master";

        GithubRepoResponse.Owner owner = new GithubRepoResponse.Owner(userName);
        GithubRepoResponse response1 = new GithubRepoResponse(false, repoName1, owner);
        GithubRepoResponse response2 = new GithubRepoResponse(true, repoName2, owner);

        GithubBranchResponse.Commit commit = new GithubBranchResponse.Commit(commitSha);
        GithubBranchResponse githubBranchResponse = new GithubBranchResponse(branchName, commit);

        GithubBranchResponse.Commit commit2 = new GithubBranchResponse.Commit(commitSha2);
        GithubBranchResponse githubBranchResponse2 = new GithubBranchResponse(branchName2, commit2);

        when(githubClient.getUser(eq(userName), any())).thenReturn(ResponseEntity.ok().build());
        when(githubClient.getReposByUser(eq(userName), any())).thenReturn(List.of(response1, response2));
        when(githubClient.getBranchesByUserAndRepo(eq(userName), eq(repoName1), any()))
                .thenReturn(List.of(githubBranchResponse, githubBranchResponse2));

        //when
        List<UserRepoMetadata> userRepoData = githubService.getUserRepoMetadata(userName);

        //then
        Assertions.assertThat(userRepoData).hasSize(1);
        UserRepoMetadata userRepo = userRepoData.get(0);
        Assertions.assertThat(userRepo.name()).isEqualTo(repoName1);
        Assertions.assertThat(userRepo.login()).isEqualTo(userName);
        Assertions.assertThat(userRepo.branches()).hasSize(2);
        Assertions.assertThat(userRepo.branches()).extracting("name")
                .containsExactlyInAnyOrder(branchName, branchName2);
        Assertions.assertThat(userRepo.branches()).extracting("sha")
                .containsExactlyInAnyOrder(commitSha, commitSha2);
    }

    @Test
    public void shouldReturnEmptyListIfAllReposAreForks() {
        //given
        String userName = "test";
        String repoName1 = "TestRepo1";
        String repoName2 = "TestRepo2";

        GithubRepoResponse.Owner owner = new GithubRepoResponse.Owner(userName);
        GithubRepoResponse response1 = new GithubRepoResponse(true, repoName1, owner);
        GithubRepoResponse response2 = new GithubRepoResponse(true, repoName2, owner);

        when(githubClient.getUser(eq(userName), any())).thenReturn(ResponseEntity.ok().build());
        when(githubClient.getReposByUser(eq(userName), any())).thenReturn(List.of(response1, response2));

        //when
        List<UserRepoMetadata> userRepoData = githubService.getUserRepoMetadata(userName);

        //then
        Assertions.assertThat(userRepoData).isEmpty();
    }

    @Test
    public void shouldReturnEmptyListIfAllReposAreEmpty() {
        //given
        String userName = "test";

        when(githubClient.getUser(eq(userName), any())).thenReturn(ResponseEntity.ok().build());
        when(githubClient.getReposByUser(eq(userName), any())).thenReturn(List.of());

        //when
        List<UserRepoMetadata> userRepoData = githubService.getUserRepoMetadata(userName);

        //then
        Assertions.assertThat(userRepoData).isEmpty();
    }
}
