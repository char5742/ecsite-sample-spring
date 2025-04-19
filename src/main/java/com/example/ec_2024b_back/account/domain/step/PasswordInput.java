package com.example.ec_2024b_back.account.domain.step;

/** パスワード検証用の入力データレコード. */
public record PasswordInput(String accountId, String hashedPassword, String rawPassword) {}
