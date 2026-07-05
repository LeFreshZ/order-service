package com.innowise.orderservice.service.implementation;

import com.innowise.orderservice.dto.UserResponse;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserServiceClient {

  private final String internalSecret;
  private final WebClient webClient;
  private final ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory;

  public UserServiceClient(
      WebClient.Builder builder,
      ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory,
      @Value("${user-service.url}") String userServiceUrl,
      @Value("${internal.secret}") String internalSecret) {

    this.webClient = builder.baseUrl(userServiceUrl).build();
    this.circuitBreakerFactory = circuitBreakerFactory;
    this.internalSecret = internalSecret;
  }

  public Optional<UserResponse> getUserById(Long userId) {
    UserResponse response = circuitBreakerFactory.create("user-service")
        .run(
            webClient.get()
                .uri("/users/{id}", userId)
                .header("X-Internal-Secret", internalSecret)
                .retrieve()
                .bodyToMono(UserResponse.class),
            ex -> {
              log.warn("Error occurred when accessing user service: {}", ex.getMessage());
              return Mono.empty();
            }
        ).block();

    return Optional.ofNullable(response);
  }
}
