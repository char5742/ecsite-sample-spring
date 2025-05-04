# 12. 用語集

このドキュメントでは、`ecsite-v2` プロジェクトに関連するビジネス用語、技術用語、略語などを定義します。新しい開発者がプロジェクト固有の用語を理解する助けとなります。

## ビジネス用語

| 用語 | 説明 |
|------|------|
| ECサイト | Electronic Commerce Site (電子商取引サイト)の略。インターネット上で商品やサービスの売買を行うWebサイト。 |

## アーキテクチャ・設計用語

| 用語 | 説明 | 本プロジェクトでの使用例 |
|------|------|----------------------|
| DDD (Domain-Driven Design) | ドメインモデルを中心に据えた設計手法。ビジネスロジックをドメイン層に集中させ、技術的な実装の詳細から分離する。 | ドメインモデル(`Account`)、リポジトリインターフェース(`Accounts`)、ドメインサービス(`AccountFactory`) |
| オニオンアーキテクチャ | 依存関係の方向を内側に向けた多層アーキテクチャ。中心にドメインモデル、その外側にアプリケーション層、さらに外側にインフラストラクチャ層を配置。 | `domain`, `application`, `infrastructure` パッケージ構造 |
| モジュラーモノリス | 単一のアプリケーションとしてデプロイされるが、内部的には明確な境界を持つモジュールに分割されたアーキテクチャ。 | `auth`, `share` モジュールの分離と明確なインターフェース |
| リアクティブプログラミング | 非同期データストリームを使用したプログラミングパラダイム。イベント駆動型でバックプレッシャーを処理できる。 | `Mono<T>`, `Flux<T>`を使用した非同期処理 |
| ドメインモデル | ビジネス領域の概念やルールを表現するオブジェクトモデル。 | `Account`, `Authentication`, `Cart`, `Order`, `Payment`, `OrderStatus`, `PaymentStatus`など |
| Value Object (VO) | 同一性ではなく属性値によって識別される不変なオブジェクト。 | `Email`, `AccountId`, `ProductId`, `CartId`, `OrderId`, `PaymentId` |
| Entity (エンティティ) | 同一性を持ち、ライフサイクルを通じて変化する可能性のあるオブジェクト。 | `Account`, `Cart`, `Order`, `Payment` (ID による同一性) |
| Factory Pattern (ファクトリーパターン) | オブジェクト生成のロジックを専用のクラスに集約する設計パターン。 | `ProductFactory`, `CategoryFactory` |
| Aggregate (集約) | 一貫性の境界を形成するエンティティと値オブジェクトのクラスター。 | `Account` と関連する `Authentication`, `Cart` と関連する `CartItem` |
| Repository (リポジトリ) | 集約の永続化と取得を抽象化するインターフェース。 | `Accounts`, `Carts`, `Orders`, `Payments` インターフェース |
| Application Service (アプリケーションサービス) | ユースケースを実装し、ドメインオブジェクトを調整するサービス。 | `LoginUsecase`, `SignupUsecase` |
| DTO (Data Transfer Object) | レイヤー間でデータを受け渡すためのオブジェクト。 | APIリクエスト/レスポンスのDTOクラス |
| ワークフロー | 一連の処理ステップを定義・実行するパターン。 | `LoginWorkflow`, `SignupWorkflow`, `AddItemToCartWorkflow`, `CreateOrderFromCartWorkflow` |
| ステップ | ワークフロー内の個別の処理単位。 | `FindAccountByEmailStep`, `VerifyWithPasswordStep` |

## プロジェクト技術スタック

| 技術 | 説明 | バージョン | 使用目的 |
|------|------|----------|---------|
| Java | オブジェクト指向プログラミング言語 | 23 | アプリケーション開発の基盤言語 |
| Spring Boot | Javaアプリケーションフレームワーク | 3.4.3 | アプリケーション構築・設定の簡素化 |
| Spring Modulith | モジュラーモノリスの開発支援ライブラリ | 1.4.0-M3 | モジュール間の依存関係管理 |
| Spring WebFlux | Springのリアクティブウェブフレームワーク | Spring Boot同梱 | リアクティブエンドポイント実装 |
| Project Reactor | リアクティブストリーム実装 | Spring Boot同梱 | 非同期・ノンブロッキング処理 (`Mono`, `Flux`) |
| MongoDB | ドキュメント指向NoSQLデータベース | 7.x | データ永続化 |
| Spring Data MongoDB Reactive | MongoDBのリアクティブドライバ | Spring Boot同梱 | MongoDB へのリアクティブアクセス |
| Spring Security | セキュリティフレームワーク | Spring Boot同梱 | 認証・認可機能の実装 |
| JWT (JSON Web Token) | 認証情報の安全な受け渡しの仕組み | Auth0 Java JWT 4.4.0 | 認証トークンの生成・検証 |
| JSpecify | Javaのnull安全性アノテーション | 1.0.0 | null参照バグの防止 |
| NullAway | コンパイル時のnull参照チェックツール | 0.12.6 | null参照バグの防止 |
| Lombok | ボイラープレート削減ツール | 1.18.38 | getter/setterなどの自動生成 |
| Spotless | コードフォーマッター | 7.0.3 | コードスタイルの統一 |
| Error Prone | 静的解析ツール | 2.37.0 | コンパイル時のバグ検出 |
| Testcontainers | テスト用コンテナ管理 | JUnit5統合版 | 結合テスト時のMongoDB環境提供 |
| Gradle | ビルドツール | Wrapper 8.8 | ビルド・テスト・依存関係管理 |
| Lefthook | Gitフック管理ツール | 1.5.5 | コミット前の自動チェック |

## モジュール構成

| モジュール | 責務 | 主要コンポーネント |
|-----------|------|------------------|
| `auth` | 認証・ユーザーアカウント管理 | `Account`, `Authentication`, `LoginWorkflow`, `SignupWorkflow` |
| `userprofile` | ユーザー情報・住所管理 | `UserProfile`, `Address`, `AddressFactory`, `CreateUserProfileWorkflow` |
| `product` | 商品・カテゴリ・在庫・プロモーション管理 | `Product`, `Category`, `Inventory`, `Promotion`, `ProductFactory`, `CategoryFactory` |
| `shopping` | カート・注文・決済管理 | `Cart`, `CartItem`, `Order`, `OrderItem`, `Payment`, `CartEvent`, `OrderEvent`, `PaymentEvent` |
| `share` | 複数モジュールで共有されるコンポーネント | `Email`, `IdGenerator`, `DomainException` |

## 重要な略語

| 略語 | 正式名称 | 説明 |
|------|---------|------|
| API | Application Programming Interface | アプリケーションの機能を外部から利用するためのインターフェース |
| CI | Continuous Integration | 継続的インテグレーション。自動ビルド・テスト実行のプロセス |
| CD | Continuous Delivery / Deployment | 継続的デリバリー/デプロイ。自動リリースプロセス |
| CSRF | Cross-Site Request Forgery | Webアプリケーションの脆弱性の一種 |
| DDD | Domain-Driven Design | ドメイン駆動設計。ビジネスドメインを中心に据えた設計手法 |
| DTO | Data Transfer Object | レイヤー間でのデータ受け渡し用オブジェクト |
| EC | Electronic Commerce | 電子商取引 |
| E2E | End-to-End | ユーザーの操作を模倣する総合テスト |
| JWT | JSON Web Token | JSONベースの認証トークン形式 |
| JVM | Java Virtual Machine | Javaプログラムを実行する仮想マシン |
| PR | Pull Request | コード変更の提案・レビュープロセス |
| RBAC | Role-Based Access Control | 役割ベースのアクセス制御 |
| VO | Value Object | 値オブジェクト。同一性ではなく内容で識別される不変オブジェクト |
| WIP | Work In Progress | 作業中・未完成の状態を示す |