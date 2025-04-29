# 12. 用語集

このドキュメントでは、`ecsite-v2` プロジェクトに関連するビジネス用語、技術用語、略語などを定義します。

## ビジネス用語

*   **ECサイト:** Electronic Commerce Site (電子商取引サイト) の略。
*   **(その他、プロジェクト固有のビジネス用語があれば記載)**

## 技術用語

*   **API (Application Programming Interface):** ソフトウェアコンポーネントが互いにやり取りするためのインターフェース。
*   **Auth0 Java JWT:** JWT (JSON Web Token) の生成・検証を行うための Java ライブラリ。
*   **CI (Continuous Integration):** 継続的インテグレーション。コード変更を頻繁にリポジトリにマージし、自動ビルド・テストを実行すること。
*   **CD (Continuous Delivery/Deployment):** 継続的デリバリー/デプロイ。CIに加えて、ビルドされたコードを自動的にテスト環境や本番環境にリリースすること。
*   **Delegateパターン:** OpenAPI Generatorで生成されるインターフェースの実装を委譲する設計パターン。
*   **Docker:** コンテナ化技術。アプリケーションとその依存関係をパッケージ化し、独立した環境で実行する。
*   **Domain Model:** ビジネス領域の概念やルールを表現するオブジェクトモデル。
*   **Error Prone:** Google が開発した Java コードの静的解析ツール。コンパイル時に一般的なエラーを検出する。
*   **Gradle:** Javaプロジェクト向けのビルド自動化ツール。
*   **Gradle Wrapper:** 特定バージョンのGradleをプロジェクトに同梱し、開発者間でビルド環境を統一する仕組み。
*   **GraalVM:** 高性能な Java Virtual Machine (JVM) および Native Image コンパイラ。
*   **Guava:** Google による Java 用のコアライブラリ群。コレクション、キャッシュ、並行処理などのユーティリティを提供。
*   **JDK (Java Development Kit):** Javaアプリケーションを開発・実行するためのソフトウェア開発キット。
*   **jMolecules:** ドメイン駆動設計 (DDD) やクリーンアーキテクチャなどの概念を Java コードで表現するためのライブラリ。
*   **JSpecify:** Java コードの null 安全性を向上させるためのアノテーション仕様 (`@NullMarked`, `@Nullable` など)。
*   **JWT (JSON Web Token):** 認証や情報交換に使われる、コンパクトで自己完結したJSONベースのトークン。
*   **Lefthook:** Git フックを管理するためのツール。コミット前などに自動チェックやフォーマットを実行できる。
*   **Lombok:** アノテーションを使用して Java の定型コード (getter/setter など) を自動生成するライブラリ。
*   **MongoDB:** ドキュメント指向のNoSQLデータベース。
*   **Modulith (Spring Modulith):** モノリシックアーキテクチャ内でモジュール性を高めるためのSpringプロジェクト。
*   **NoSQL:** Not Only SQL。リレーショナルデータベース以外のデータベース管理システムの総称。
*   **NullAway:** Error Prone プラグイン。コンパイル時に NullPointerException の可能性を検出する。
*   **OpenAPI Specification:** RESTful APIを記述するための標準仕様。以前はSwagger Specificationと呼ばれていた。
*   **OpenAPI Generator:** OpenAPI 仕様から API クライアントやサーバーコードを自動生成するツール。
*   **Project Reactor:** Spring WebFluxで使用される、ノンブロッキングなリアクティブプログラミングライブラリ。
*   **Pull Request (PR):** Gitリポジトリにおいて、あるブランチから別のブランチへのコード変更のマージを提案・レビューする仕組み。
*   **Reactive Programming:** 非同期なデータストリームを扱うプログラミングパラダイム。
*   **RESTful API:** Representational State Transfer の原則に基づいたWeb API設計スタイル。
*   **Spotless:** コードフォーマッターツール。
*   **Spring Boot:** JavaベースのWebアプリケーションフレームワークであるSpringを簡単に利用できるようにするプロジェクト。
*   **Spring Security:** Springベースのアプリケーションに認証・認可機能を提供するフレームワーク。
*   **Spring WebFlux:** Spring Framework 5で導入された、リアクティブスタックのWebフレームワーク。
*   **SpringDoc OpenAPI:** Spring Boot アプリケーションで OpenAPI 3 ドキュメントを自動生成するためのライブラリ。Swagger UI も統合。
*   **Swagger UI:** OpenAPI仕様に基づいてAPIドキュメントを視覚的に表示し、対話的に操作できるツール。
*   **Testcontainers:** Dockerコンテナを利用して、テスト実行時にデータベースなどの依存サービスを簡単に起動できるJavaライブラリ。
*   **Value Object:** 値そのものによって識別される不変なオブジェクト（例: `Email`, `AccountId`, `JsonWebToken`)。

## 略語

*   **API:** Application Programming Interface
*   **CI:** Continuous Integration
*   **CD:** Continuous Delivery / Continuous Deployment
*   **CSRF:** Cross-Site Request Forgery
*   **DDD:** Domain-Driven Design
*   **DTO:** Data Transfer Object
*   **EC:** Electronic Commerce
*   **E2E:** End-to-End (テスト)
*   **IDE:** Integrated Development Environment (統合開発環境)
*   **JDK:** Java Development Kit
*   **JWT:** JSON Web Token
*   **JVM:** Java Virtual Machine
*   **PR:** Pull Request
*   **RBAC:** Role-Based Access Control
*   **VO:** Value Object
*   **WIP:** Work In Progress

*(その他、プロジェクト固有の略語があれば記載)*
