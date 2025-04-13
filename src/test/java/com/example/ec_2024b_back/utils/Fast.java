package com.example.ec_2024b_back.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Tag;

/** SpringBootTestなどを使用しない、メソッド単体テスト */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag("fast")
public @interface Fast {}
