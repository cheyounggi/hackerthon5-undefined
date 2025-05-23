package org.server.core.member.api.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GithubTokenRequest(
        String clientId,
        String clientSecret,
        String code,
        String redirectUri
) {
}
