package com.example.ec_2024b_back.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/** 統合テストを示すためのカスタムアノテーション. JUnit 5の @Tag("IntegrationTest") をメタアノテーションとして使用します. */
@Target({ElementType.TYPE, ElementType.METHOD}) // クラスとメソッドに適用可能
@Retention(RetentionPolicy.RUNTIME) // 実行時にアノテーション情報を保持
@Tag("IntegrationTest") // JUnit 5のタグ付け
public @interface IntegrationTest {}
