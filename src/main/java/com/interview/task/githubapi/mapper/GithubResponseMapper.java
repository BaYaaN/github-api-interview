package com.interview.task.githubapi.mapper;

import com.interview.task.githubapi.model.domain.UserRepoMetadata;
import com.interview.task.githubapi.model.dto.GithubBranchResponse;
import com.interview.task.githubapi.model.dto.GithubRepoResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GithubResponseMapper {

    public UserRepoMetadata mapToRepoData(GithubRepoResponse repoResponse, List<GithubBranchResponse> branchResponse) {
        return new UserRepoMetadata(repoResponse.name(), repoResponse.owner().login(),
                branchResponse
                        .stream()
                        .map(branch -> new UserRepoMetadata.Branch(branch.name(), branch.commit().sha()))
                        .toList());
    }
}
