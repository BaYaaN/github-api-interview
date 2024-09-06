package com.interview.task.githubapi.model.dto;

public record GithubBranchResponse(String name, Commit commit) {
    public record Commit(String sha) {
    }
}