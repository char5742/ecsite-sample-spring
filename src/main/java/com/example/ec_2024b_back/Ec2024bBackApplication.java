package com.example.ec_2024b_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/** ECサイトバックエンドのメインアプリケーションクラス. アプリケーションの起動とルーティング設定を行います。 */
@SpringBootApplication
@Import(RouterConfig.class)
@SuppressWarnings({"FinalClass", "PrivateConstructorForUtilityClass"})
public class Ec2024bBackApplication {

  public static void main(String[] args) {
    SpringApplication.run(Ec2024bBackApplication.class, args);
  }
}
