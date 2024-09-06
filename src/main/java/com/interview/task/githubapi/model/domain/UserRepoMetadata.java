package com.interview.task.githubapi.model.domain;

import java.util.List;

public record UserRepoMetadata(String name, String login, List<Branch> branches) {
    public record Branch(String name, String sha) {
    }
}
