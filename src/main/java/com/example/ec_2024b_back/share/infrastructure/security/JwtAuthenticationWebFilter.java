package com.example.ec_2024b_back.share.infrastructure.security;

import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/** JWT認証用WebFluxフィルター. AuthorizationヘッダーからJWTを抽出し、検証・認証情報をSecurityContextにセットする。 */
@Slf4j
@AllArgsConstructor
@Component
public class JwtAuthenticationWebFilter implements WebFilter {

  private final JsonWebTokenProvider jwtProvider;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      try {
        String userId = jwtProvider.extractUserId(token);
        return jwtProvider
            .validateToken(token, userId)
            .flatMap(
                valid -> {
                  if (Boolean.TRUE.equals(valid)) {
                    Authentication auth =
                        new UsernamePasswordAuthenticationToken(
                            userId, null, Collections.emptyList());
                    return chain
                        .filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                  }
                  return chain.filter(exchange);
                })
            .onErrorResume(
                e -> {
                  log.warn("JWT検証に失敗しました: {}", e.getMessage());
                  return chain.filter(exchange);
                });
      } catch (Exception e) {
        log.warn("JWTからユーザーIDの抽出に失敗しました: {}", e.getMessage());
        // 認証失敗時は認証情報なしで続行
        return chain.filter(exchange);
      }
    }
    // JWTがない場合はそのまま続行
    return chain.filter(exchange);
  }
}
