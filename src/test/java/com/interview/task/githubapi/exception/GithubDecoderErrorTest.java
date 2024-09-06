package com.interview.task.githubapi.exception;

import com.interview.task.githubapi.config.GithubErrorDecoder;
import com.interview.task.githubapi.exception.ResourceNotFoundException;
import com.interview.task.githubapi.exception.UnsupportedMediaTypeException;
import feign.Request;
import feign.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.Map;

public class GithubDecoderErrorTest {

    GithubErrorDecoder githubErrorDecoder = new GithubErrorDecoder();

    @Test
    public void shouldThrowNotFoundResourceException() {
        String methodKey = "key";
        Request request = Request.create(Request.HttpMethod.GET, "http://github-api/users", Map.of(), "Body".getBytes(), Charset.defaultCharset(), null);
        Response notFound = Response.builder()
                .reason("NotFound")
                .status(404)
                .request(request)
                .build();

        Assertions.assertThatThrownBy(() -> githubErrorDecoder.decode(methodKey, notFound))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void shouldUnsupportedMediaTypeException() {
        String methodKey = "key";
        Request request = Request.create(Request.HttpMethod.GET, "http://github-api/users", Map.of(), "Body".getBytes(), Charset.defaultCharset(), null);
        Response notFound = Response.builder()
                .reason("Unsupported media type")
                .status(415)
                .request(request)
                .build();

        Assertions.assertThatThrownBy(() -> githubErrorDecoder.decode(methodKey, notFound))
                .isInstanceOf(UnsupportedMediaTypeException.class);
    }
}
