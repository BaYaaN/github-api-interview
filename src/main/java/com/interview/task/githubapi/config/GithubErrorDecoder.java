package com.interview.task.githubapi.config;

import com.interview.task.githubapi.exception.ResourceNotFoundException;
import com.interview.task.githubapi.exception.UnsupportedMediaTypeException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class GithubErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus responseStatus = HttpStatus.valueOf(response.status());

        if (responseStatus.is4xxClientError()) {
            if (responseStatus == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException(buildMessage(response));
            }

            if (responseStatus == HttpStatus.UNSUPPORTED_MEDIA_TYPE) {
                throw new UnsupportedMediaTypeException(buildMessage(response));
            }

            return new IllegalArgumentException(buildMessage(response));
        } else {
            return new RuntimeException(buildMessage(response));
        }
    }

    private String buildMessage(Response response) {
        return "Error response reason: " + response.reason() + " for resource " + response.request().url();
    }
}


