# Auth モジュール

このドキュメントでは、`auth` モジュールの責務、ドメインモデル、主要な機能について説明します。

## 責務

`auth` モジュールは、ユーザー認証とアカウント作成に関連する以下の主要な責務を担当します。

*   **認証 (ログイン):**
    *   ユーザーの本人確認を行います。現在はメールアドレスとパスワードによる認証をサポートしています。
    *   認証成功時には、後続のリクエストで使用するための認証情報（JWT）を発行します。
*   **アカウント作成 (サインアップ):**
    *   メールアドレスとパスワードを使用して新しいアカウントを作成します。
*   **アカウント管理 (一部未実装):**
    *   アカウント情報の参照・更新 (将来実装予定)
    *   パスワード変更・リセット (将来実装予定)
    *   退会処理 (将来実装予定)

## ドメインモデル

`auth` モジュールの主要なドメインモデルは以下の通りです。

*   **`Account` (Record):**
    *   責務: ユーザーアカウント全体を表す集約ルート。
    *   主要プロパティ:
        *   `id`: アカウントID (`AccountId` 値オブジェクト)。
        *   `authentications`: このアカウントに関連付けられた認証方法のリスト (`ImmutableList<Authentication>`)。
        *   `domainEvents`: アカウントに関連するドメインイベントのリスト (`ImmutableList<DomainEvent>`)。
    *   ファクトリメソッド:
        *   `create`: 新しいアカウントを作成し、`AccountCreated`イベントを発行。
        *   `reconstruct`: 永続化されたアカウントを再構築（イベントなし）。

*   **`AccountId` (Record, Value Object):**
    *   責務: アカウントの一意な識別子。
    *   プロパティ: `value` (String)。空でないことが保証されます。

*   **`Authentication` (Sealed Interface):**
    *   責務: 認証方法の共通インターフェース。現在は `EmailAuthentication` のみが許可されています（将来的に他の認証方法、例: OAuth を追加可能）。

*   **`EmailAuthentication` (Record):**
    *   責務: メールアドレスとパスワードによる認証方法を表現。
    *   主要プロパティ:
        *   `email`: ユーザーのメールアドレス (`Email` 値オブジェクト、`share` モジュールで定義)
        *   `password`: パスワード（ハッシュ化されたもの）

*   **`JsonWebToken` (Record, Value Object):**
    *   責務: JWT トークンを表す値オブジェクト。
    *   プロパティ: `value` (String)。空でないことが保証されます。

## リポジトリ

`auth` モジュールは、クリーンアーキテクチャの原則に従ってリポジトリを設計しています。

*   **`Accounts` (Interface):**
    *   責務: ドメイン層で定義されたリポジトリインターフェース。アカウント集約の永続化を抽象化します。
    *   主要メソッド:
        *   `findByEmail(Email email)`: メールアドレスでアカウントを検索
        *   `save(Account account)`: アカウントを保存 (新規作成または更新)

*   **`MongoAccounts` (Class):**
    *   責務: インフラ層で実装されたリポジトリ。`Accounts` インターフェースを実装し、Spring Data MongoDB Reactive を内部で利用してデータアクセスを行います。
    *   ドメインモデル (`Account`) とドキュメントモデル (`AccountDocument`) の相互変換を担当します。

*   **`AccountDocument` (Class):**
    *   責務: MongoDB の `accounts` コレクションに対応するドキュメントモデル。
    *   メソッド: `toDomain()` でドメインモデルに変換、`fromDomain(Account account)` static ファクトリメソッドでドキュメントモデルを生成。

## 主要フロー

`auth` モジュールは以下の主要なフローを実装しています：

### ログインフロー

1.  クライアントが `/api/authentication/login` エンドポイントにメールアドレスとパスワードを送信。
2.  `LoginWithEmailHandler` (Interfaces レイヤー) がリクエストを受け取り、`LoginUsecase` (Application レイヤー) を呼び出す。
3.  `LoginUsecase` が `LoginWorkflow` (Domain レイヤー) を実行。
4.  `LoginWorkflow` が以下のステップ (Domain レイヤーのインターフェース) を順に実行:
    1.  `FindAccountByEmailStep`: メールアドレスでアカウントを検索 (Infrastructure レイヤーの `FindAccountByEmailStepImpl` が実行される)。
    2.  `VerifyWithPasswordStep`: 提供されたパスワードと保存されているハッシュ化パスワードを検証 (Infrastructure レイヤーの `VerifyWithPasswordStepImpl` が実行される)。
    3.  `GenerateJWTStep`: 認証成功時にJWTトークンを生成 (Infrastructure レイヤーの `GenerateJWTStepImpl` が実行される)。
5.  生成された `JsonWebToken` をクライアントに返却。

### サインアップフロー

1.  クライアントが `/api/authentication/signup` エンドポイントにメールアドレスとパスワードを送信。
2.  `SignupWithEmailHandler` (Interfaces レイヤー) がリクエストを受け取り、`SignupUsecase` (Application レイヤー) を呼び出す。
3.  `SignupUsecase` が `SignupWorkflow` (Domain レイヤー) を実行。
4.  `SignupWorkflow` が以下のステップ (Domain レイヤーのインターフェース) を実行:
    1.  `CreateAccountWithEmailStep`: 新しいアカウントエンティティを作成し、リポジトリ (`Accounts`) を通じて保存 (Infrastructure レイヤーの `CreateAccountWithEmailStepImpl` が実行される)。
       - `AccountFactory`を使用してアカウントを作成し、ドメインイベント (`AccountCreated`) を発行。
       - `IdGenerator` (share モジュール) を使用して一意なIDを生成。
5.  成功レスポンス (サインアップ成功メッセージ) をクライアントに返却。

### トークン認証フロー (Spring Security連携)

1.  保護されたAPIにアクセスする際、クライアントはリクエストヘッダーにJWTトークンを設定 (`Authorization: Bearer <token>`)。
2.  Spring Security のフィルタチェーン (設定が必要) がリクエストからトークンを抽出。
3.  `JsonWebTokenProvider` (Infrastructure レイヤー) を使用してトークンを検証。
4.  トークンが有効な場合、認証情報 (`Authentication` オブジェクト) を `SecurityContext` に設定。
5.  Spring Security が後続の処理で API アクセス権限を確認し、認可を実施。

## 主要コンポーネント

*   **Interfaces レイヤー:**
    *   `LoginWithEmailHandler`: ログインAPIのリクエストハンドラー。
    *   `SignupWithEmailHandler`: サインアップAPIのリクエストハンドラー。
*   **Application レイヤー:**
    *   `LoginUsecase`: ログイン処理を統括するユースケースクラス。
    *   `SignupUsecase`: サインアップ処理を統括するユースケースクラス。
*   **Domain レイヤー:**
    *   `Account`, `AccountId`, `Authentication`, `EmailAuthentication`, `JsonWebToken`: ドメインモデル。
    *   `Accounts`: リポジトリインターフェース。
    *   `AccountFactory`: アカウント作成を担当するドメインサービス。
    *   `LoginWorkflow`: ログイン処理の各ステップを調整するワークフロークラス。
    *   `SignupWorkflow`: サインアップ処理のステップを調整するワークフロークラス。
    *   `FindAccountByEmailStep`, `VerifyWithPasswordStep`, `GenerateJWTStep`, `CreateAccountWithEmailStep`: 各処理ステップのインターフェース。
*   **Infrastructure レイヤー:**
    *   `MongoAccounts`: `Accounts` リポジトリの実装。
    *   `AccountDocument`: MongoDBドキュメントモデル。
    *   `FindAccountByEmailStepImpl`, `VerifyWithPasswordStepImpl`, `GenerateJWTStepImpl`, `CreateAccountWithEmailStepImpl`: 各ステップインターフェースの実装。
    *   `JsonWebTokenProvider`: JWT の生成と検証を担当。
    *   `JWTProperties`: JWT署名キー、有効期限などの設定管理。

## 設計上のポイント

1.  **クリーンアーキテクチャの採用:**
    *   ドメイン層はインフラ層やアプリケーション層に依存しない。
    *   リポジトリやステップのインターフェースはドメイン層で定義し、実装はインフラ層に配置。
2.  **値オブジェクトの活用:**
    *   `AccountId`, `JsonWebToken`, `Email` (share) など、不変で自己検証ロジックを持つ値オブジェクトとして Java Record を活用。
3.  **責務の分離 (ステップパターン):**
    *   認証やアカウント作成の各処理を単一責任のステップ (インターフェースと実装) に分割。
    *   ワークフロークラスがこれらのステップを協調させることで、拡張性と保守性を向上。
4.  **リアクティブプログラミング:**
    *   非同期処理を効率的に扱うため Project Reactor を活用。
    *   リポジトリ操作やステップ実行は `Mono` を返し、リアクティブなデータフローを実現。
