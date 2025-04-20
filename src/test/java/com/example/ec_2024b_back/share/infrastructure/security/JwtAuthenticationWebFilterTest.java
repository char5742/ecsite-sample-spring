package com.example.ec_2024b_back.share.infrastructure.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class JwtAuthenticationWebFilterTest {
  private JsonWebTokenProvider jwtProvider;
  private JwtAuthenticationWebFilter filter;

  @BeforeEach
  void setUp() {
    jwtProvider = mock(JsonWebTokenProvider.class);
    filter = new JwtAuthenticationWebFilter(jwtProvider);
  }

  @Test
  void filter_noAuthorizationHeader_continuesWithoutContext() {
    var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
    var chain = mock(WebFilterChain.class);
    when(chain.filter(exchange)).thenReturn(Mono.empty());

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    verify(chain).filter(exchange);
    verifyNoInteractions(jwtProvider);
  }

  @Test
  void filter_invalidTokenExtraction_continuesWithoutContext() {
    var token = "badtoken";
    when(jwtProvider.extractUserId(token)).thenThrow(new RuntimeException("extract fail"));

    var exchange =
        MockServerWebExchange.from(
            MockServerHttpRequest.get("/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build());

    var chain = mock(WebFilterChain.class);
    when(chain.filter(exchange)).thenReturn(Mono.empty());

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    verify(chain).filter(exchange);
    verify(jwtProvider).extractUserId(token);
  }

  @Test
  void filter_validateFails_continuesWithoutContext() {
    var token = "valtoken";
    var userId = "user123";
    when(jwtProvider.extractUserId(token)).thenReturn(userId);
    when(jwtProvider.validateToken(token, userId)).thenReturn(Mono.just(false));

    var exchange =
        MockServerWebExchange.from(
            MockServerHttpRequest.get("/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build());

    var chain = mock(WebFilterChain.class);
    when(chain.filter(exchange)).thenReturn(Mono.empty());

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    verify(chain).filter(exchange);
    verify(jwtProvider).validateToken(token, userId);
  }

  @Test
  void filter_validToken_setsSecurityContext() {
    var token = "goodtoken";
    var userId = "user123";
    when(jwtProvider.extractUserId(token)).thenReturn(userId);
    when(jwtProvider.validateToken(token, userId)).thenReturn(Mono.just(true));

    var exchange =
        MockServerWebExchange.from(
            MockServerHttpRequest.get("/secure")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build());

    var chain = mock(WebFilterChain.class);
    when(chain.filter(any())).thenReturn(Mono.empty());

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    verify(chain).filter(any());
    verify(jwtProvider).validateToken(token, userId);
  }

  @Test
  void filter_validateError_continuesWithoutContext() {
    var token = "errtoken";
    var userId = "userXYZ";
    when(jwtProvider.extractUserId(token)).thenReturn(userId);
    when(jwtProvider.validateToken(token, userId))
        .thenReturn(Mono.error(new RuntimeException("validation error")));

    var exchange =
        MockServerWebExchange.from(
            MockServerHttpRequest.get("/error")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build());

    var chain = mock(WebFilterChain.class);
    when(chain.filter(exchange)).thenReturn(Mono.empty());

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    verify(chain).filter(exchange);
    verify(jwtProvider).validateToken(token, userId);
  }
}
