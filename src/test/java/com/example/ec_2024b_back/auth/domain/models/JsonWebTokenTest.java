package com.example.ec_2024b_back.auth.domain.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.ec_2024b_back.utils.Fast;
import org.junit.jupiter.api.Test;

@Fast
class JsonWebTokenTest {

    @Test
    void constructor_shouldCreateInstance_whenValidTokenProvided() {
        var token = new JsonWebToken("valid-token");
        assertThat(token.value()).isEqualTo("valid-token");
    }

    @Test
    void constructor_shouldThrowException_whenEmptyTokenProvided() {
        assertThatThrownBy(() -> new JsonWebToken(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("JWTトークンはnullまたは空であってはいけません");
    }
}
