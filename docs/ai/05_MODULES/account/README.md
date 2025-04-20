# Account モジュール

このドキュメントでは、`account` モジュールの責務、ドメインモデル、主要な機能について説明します。

## 責務

`account` モジュールは、ユーザーアカウントに関連する以下の主要な責務を担当します。

*   **認証:**
    *   ユーザーの本人確認を行います。現在はメールアドレスとパスワードによる認証をサポートしています。
    *   認証成功時には、後続のリクエストで使用するための認証情報（例: JWT）を発行します。
*   **アカウント管理:** (将来実装予定)
    *   アカウントの新規作成
    *   アカウント情報の参照・更新
    *   パスワード変更・リセット
    *   退会処理

## ドメインモデル

`account` モジュールの主要なドメインモデルは以下の通りです。

*   **`Account` (Record):**
    *   責務: ユーザーアカウント全体を表す集約ルート。
    *   主要プロパティ:
        *   `id`: アカウントID (`AccountId` 値オブジェクト)。
        *   `authentications`: このアカウントに関連付けられた認証方法のリスト (`List<IAuthentication>`)。
*   **`AccountId` (Record, Value Object):**
    *   責務: アカウントの一意な識別子。
    *   プロパティ: `id` (String)。空でないことが保証されます。
*   **`IAuthentication` (Sealed Interface):**
    *   責務: 認証方法の共通インターフェース。現在は `EmailAuthentication` のみが許可されています (将来的に他の認証方法、例: OAuth を追加可能)。

## 認証フロー

`account` モジュールは以下の認証フローを実装しています：

### ログインフロー

1. クライアントが `/api/authentication/login` エンドポイントにメールアドレスとパスワードを送信
2. `AuthenticationService` がメールアドレスでユーザーを検索し、パスワードを検証
3. 認証成功時に `JsonWebTokenProvider` がJWTトークンを生成
4. ユーザー情報とトークンを含むレスポンスをクライアントに返却

### トークン認証フロー

1. 保護されたAPIにアクセスする際、クライアントはリクエストヘッダーにJWTトークンを設定
   ```
   Authorization: Bearer [jwt-token]
   ```
2. `JwtAuthenticationWebFilter` がリクエストから「Authorization」ヘッダーを抽出
3. ヘッダーからトークンを取り出し、`JsonWebTokenProvider` を使用して検証
4. トークンが有効な場合、`ReactiveSecurityContextHolder` に認証情報を設定
5. Spring SecurityがAPIアクセス権限を確認し、認可を実施

## 主要クラス

* **インフラストラクチャ層:**
  * `SecurityConfig`: Spring Securityの設定クラス。URL単位のアクセス制御、認証フィルターの設定を担当
  * `JwtAuthenticationWebFilter`: WebFlux用のJWT認証フィルター。トークンの抽出・検証・認証情報の設定を実行
  
* **共通基盤層 (share モジュール):**
  * `JsonWebTokenProvider`: JWT (JSON Web Token) の生成と検証を担当
  * `JWTProperties`: JWT署名キー、有効期限などの設定管理
