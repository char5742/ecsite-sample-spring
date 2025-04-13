# プロジェクト構造

本ドキュメントは、`ecsite-v2`プロジェクトの構造と主要コンポーネントについて説明します。

## 概要

このプロジェクトは、Spring Boot 3.5.0ベースのECサイトのバックエンドアプリケーションで、以下の特徴があります：

- **Java 23** を使用
- **Spring Modulith** によるモジュラー設計
- **Spring WebFlux** によるリアクティブプログラミング
- **MongoDB Reactive** によるデータストア
- **Spring Security** による認証・認可
- **OpenAPI/Swagger** によるAPI定義と自動生成

## プロジェクト構成

### モジュール構成

プロジェクトは以下のモジュールで構成されています：

1. **ルートプロジェクト** : `ec-2024b-back` - メインアプリケーション
2. **api-model** : OpenAPI定義から自動生成されるAPIモデルを含むサブモジュール

### ディレクトリ構造

```
ecsite-v2/
├── api-model/               # APIモデルサブモジュール
│   ├── build.gradle        # OpenAPI Generator設定を含む
│   └── src/                # 自動生成されたコードの格納場所
├── docs/
│   └── openapi/            # OpenAPI定義ファイル
│       └── entry.yml       # メインのOpenAPI仕様ファイル
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/ec_2024b_back/
│   │   │       ├── Ec2024bBackApplication.java  # メインアプリケーションクラス
│   │   │       ├── account/                     # アカウント管理モジュール
│   │   │       ├── user/                        # ユーザー管理モジュール
│   │   │       └── share/                       # 共有コンポーネント
│   │   └── resources/     # 設定ファイルやリソース
│   └── test/              # テストコード
├── build.gradle           # メインのGradleビルド設定
└── spotless.gradle        # コード整形ルール
```

## 技術スタック

### フレームワーク・ライブラリ

- **Spring Boot 3.5.0** : アプリケーションフレームワーク
- **Spring Modulith** : モジュラーモノリスアーキテクチャの実現
- **Spring WebFlux** : リアクティブWebスタック
- **Spring Data MongoDB Reactive** : MongoDBのリアクティブドライバ
- **Spring Security** : 認証・認可
- **Lombok** : ボイラープレートコードの削減
- **SpringDoc OpenAPI** : API ドキュメント生成
- **JSpecify** : null許容性アノテーション

### ビルド・ツール

- **Gradle** : ビルドツール
- **OpenAPI Generator** : API定義からのコード生成
- **GraalVM Native Build Tools** : ネイティブイメージ生成
- **Spotless** : コード整形

## モジュール設計

プロジェクトはSpring Modulithを使用したモジュラーモノリスアーキテクチャを採用しています。

### モジュール

1. **account** : アカウント管理に関する機能
   - タイプ: OPEN（他のモジュールから参照可能）
   - ドメインモデル、リポジトリ、サービスを含む

2. **user** : ユーザー管理に関する機能
   - ドメインモデルを含む

3. **share** : 共有コンポーネントやユーティリティ

各モジュールは独自の責務を持ち、明確な境界を持っています。モジュール間の依存関係は`package-info.java`ファイルで定義されています。

## API設計

APIは`docs/openapi/entry.yml`でOpenAPI形式で定義されており、以下の特徴があります：

- OpenAPI Generator（version 7.12.0）によるコード自動生成
- Spring WebFluxベースのリアクティブエンドポイント
- インターフェースのみ生成（delegateパターン）
- Java 8 DateTimeライブラリの使用
- タグによるAPIのグループ化

## ビルドと実行

プロジェクトはGradleでビルドされ、以下のコマンドで実行できます：

```shell
./gradlew bootRun  # 開発モードで実行
./gradlew build    # アプリケーションのビルド
```

## 開発ガイドライン

- **コーディングスタイル**: Spotlessを使用して一貫したコードスタイルを維持
- **モジュール境界**: モジュール間の依存関係を明示的に定義
- **リアクティブプログラミング**: ブロッキング操作を避け、Reactorのパターンに従う
- **API開発**: APIの変更は常にOpenAPI定義ファイルから開始する
