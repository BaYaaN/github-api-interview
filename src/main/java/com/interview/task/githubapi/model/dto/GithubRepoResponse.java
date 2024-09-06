package com.interview.task.githubapi.model.dto;

public record GithubRepoResponse(boolean fork, String name, Owner owner) {
    public record Owner(String login) {
    }
}
