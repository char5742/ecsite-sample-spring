# プロジェクト概要

## 主な特徴
- Spring Boot 3.4.3ベースのECサイトバックエンド
- Java 23
- Spring Modulithによるモジュラー設計
- リアクティブアーキテクチャ (WebFlux)
- MongoDB (Reactive)によるデータストア
- Spring Security認証

## 構成
```
src/
├── main/
│   ├── java/
│   │   └── com/example/ec_2024b_back/
│   │       ├── Ec2024bBackApplication.java
│   │       ├── auth/        # 認証・認可
│   │       ├── config/      # アプリケーション設定
│   │       ├── logistics/   # 配送管理
│   │       ├── product/     # 商品管理
│   │       ├── sample/      # サンプルモジュール（参考実装）
│   │       ├── share/       # 共通コンポーネント
│   │       ├── shopping/    # ショッピング機能
│   │       └── userprofile/ # ユーザー情報
│   └── resources/
└── test/
```

## 技術スタック

### 主要ライブラリ
- Spring Boot 3.4.3 / Modulith / WebFlux
- MongoDB Reactive Driver
- Lombok 1.18.38
- JSpecify 1.0.0 (Null安全性)
- jMolecules 1.10.0 (DDD実装)
- Auth0 JWT 4.4.0

### 開発ツール
- Gradle
- Spotless (コード整形)
- ErrorProne/NullAway (静的解析)
- Lefthook (Gitフック)

## ビルド・実行
```bash
./gradlew bootRun  # 開発実行
./gradlew build    # ビルド
```