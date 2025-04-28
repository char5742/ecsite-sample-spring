# Auth モジュール

このドキュメントでは、`auth` モジュールの責務、ドメインモデル、主要な機能について説明します。

## 責務

`auth` モジュールは、ユーザー認証に関連する以下の主要な責務を担当します。

*   **認証:**
    *   ユーザーの本人確認を行います。現在はメールアドレスとパスワードによる認証をサポートしています。
    *   認証成功時には、後続のリクエストで使用するための認証情報（例: JWT）を発行します。
*   **アカウント管理:** (将来実装予定)
    *   アカウントの新規作成
    *   アカウント情報の参照・更新
    *   パスワード変更・リセット
    *   退会処理

## ドメインモデル

`auth` モジュールの主要なドメインモデルは以下の通りです。

*   **`Account` (Record):**
    *   責務: ユーザーアカウント全体を表す集約ルート。
    *   主要プロパティ:
        *   `id`: アカウントID (`AccountId` 値オブジェクト)。
        *   `authentications`: このアカウントに関連付けられた認証方法のリスト (`ImmutableList<Authentication>`)。

*   **`AccountId` (Record, Value Object):**
    *   責務: アカウントの一意な識別子。
    *   プロパティ: `id` (String)。空でないことが保証されます。

*   **`Authentication` (Sealed Interface):**
    *   責務: 認証方法の共通インターフェース。現在は `EmailAuthentication` のみが許可されています（将来的に他の認証方法、例: OAuth を追加可能）。
    *   factory メソッド: `Authentication.of(String type, Map<String, String> credential)` を通じて適切な認証タイプのインスタンスを生成。

*   **`EmailAuthentication` (Record):**
    *   責務: メールアドレスとパスワードによる認証方法を表現。
    *   主要プロパティ:
        *   `type`: 認証タイプ (`AuthenticationType` で "email" を指定)
        *   `email`: ユーザーのメールアドレス (`Email` 値オブジェクト)
        *   `password`: パスワード（ハッシュ化されたもの）

*   **`JsonWebToken` (Record, Value Object):**
    *   責務: JWT トークンを表す値オブジェクト。
    *   プロパティ: `value` (String)。空でないことが保証されます。

## リポジトリ

`auth` モジュールは、クリーンアーキテクチャの原則に従ってリポジトリを設計しています。

*   **`AccountRepository` (Interface):**
    *   責務: ドメイン層で定義されたリポジトリインターフェース。
    *   主要メソッド:
        *   `findByEmail(Email email)`: メールアドレスでアカウントを検索
        *   `save(Account account)`: アカウントを保存

*   **`MongoAccountRepository` (Interface):**
    *   責務: インフラ層で実装されたリポジトリ。Spring Data MongoDB を活用。
    *   `AccountRepository` を実装し、`ReactiveMongoRepository` を継承。
    *   ドメインモデルとドキュメントモデルの変換を担当。

*   **`AccountDocument` (Class):**
    *   責務: MongoDB ドキュメントモデル。
    *   メソッド: `toDomain()` でドメインモデルに変換。

## 認証フロー

`auth` モジュールは以下の認証フローを実装しています：

### ログインフロー

1. クライアントが `/api/auth/login` エンドポイントにメールアドレスとパスワードを送信
2. `LoginWithEmailHandler` がリクエストを受け取り、`LoginUsecase` を呼び出し
3. `LoginUsecase` が `LoginWorkflow` を実行
4. `LoginWorkflow` が以下のステップを順に実行:
   1. `FindAccountByEmailStep`: メールアドレスでアカウントを検索
   2. `VerifyWithPasswordStep`: パスワードの検証
   3. `GenerateJWTStep`: 認証成功時にJWTトークンを生成
5. 生成された `JsonWebToken` をクライアントに返却

### トークン認証フロー

1. 保護されたAPIにアクセスする際、クライアントはリクエストヘッダーにJWTトークンを設定
   ```
   Authorization: Bearer [jwt-token]
   ```
2. Spring Security のフィルタチェーンがリクエストからトークンを抽出し検証
3. トークンが有効な場合、認証情報をSecurityContextに設定
4. Spring SecurityがAPIアクセス権限を確認し、認可を実施

## 主要コンポーネント

* **アプリケーション層:**
  * `LoginUsecase`: ログイン処理を統括するユースケースクラス
  
* **ドメイン層:**
  * `LoginWorkflow`: ログイン処理の各ステップを調整するワークフロークラス
  * `FindAccountByEmailStep`: メールアドレスによるアカウント検索ステップ
  * `VerifyWithPasswordStep`: パスワード検証ステップ
  * `GenerateJWTStep`: JWT生成ステップ
  
* **インフラストラクチャ層:**
  * `JsonWebTokenProvider`: JWT (JSON Web Token) の生成と検証を担当
  * `JWTProperties`: JWT署名キー、有効期限などの設定管理
  * `FindAccountByEmailStepImpl`: 検索ステップの実装
  * `VerifyWithPasswordStepImpl`: パスワード検証ステップの実装
  * `GenerateJWTStepImpl`: JWT生成ステップの実装

## 設計上のポイント

1. **クリーンアーキテクチャの採用:**
   * ドメイン層はインフラ層に依存しない
   * リポジトリインターフェースはドメイン層で定義
   * 実装はインフラ層に配置

2. **値オブジェクトの活用:**
   * `AccountId`, `JsonWebToken` など値オブジェクトとしてrecordを活用
   * 不変性の確保と自己検証機能の実装

3. **責務の分離:**
   * ステップパターンにより、各処理を単一責任のコンポーネントに分割
   * 拡張性と保守性の向上

4. **リアクティブプログラミング:**
   * 非同期処理をサポートするためReactorを活用
   * 各ステップは `Mono` を返し、リアクティブなデータフローを実現
