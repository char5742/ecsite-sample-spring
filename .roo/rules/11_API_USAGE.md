# 11. API利用ガイド

このドキュメントでは、`ecsite-v2` プロジェクトが提供するAPIの利用方法について説明します。主に開発中の動作確認やテストを目的としています。

## OpenAPI仕様

本プロジェクトでは、OpenAPIに準拠したREST APIを提供しています。API仕様はOpenAPI形式で記述され、`docs/openapi/entry.yml`で確認できます。この仕様は常に最新の状態に保たれるよう管理されています。

## Swagger UI

Swagger UIは、APIをインタラクティブに操作・テストするためのツールです。デフォルトでは無効化されていますが、以下の手順で有効化できます。

### Swagger UI有効化方法

1. `application.properties`に以下の設定を追加します：
   ```properties
   springdoc.swagger-ui.enabled=true
   ```

2. アプリケーションを起動します：
   ```bash
   ./gradlew bootRun
   ```

3. ブラウザで以下のURLにアクセスします：
   ```
   http://localhost:8080/swagger-ui.html
   ```
   (ポート番号は環境によって異なる場合があります)

### Swagger UI使用方法

1. **API一覧確認**: 画面上部にAPI一覧が表示されます。
2. **API詳細確認**: 各APIをクリックすると詳細（説明、パラメータ、リクエストボディ、レスポンススキーマ）が展開されます。
3. **API試行手順**:
   1. 試したいAPIを展開します
   2. 右上の "Try it out" ボタンをクリックします
   3. 必要なパラメータやリクエストボディを入力します
   4. "Execute" ボタンをクリックしてリクエストを送信します
   5. レスポンス（ステータスコード、レスポンスボディ、ヘッダー、curlコマンド例）が表示されます

## 認証方法

本プロジェクトのAPIは、多くのエンドポイントで認証（JWT）が必要です。以下で認証の流れを説明します。

### 1. トークン取得 (ログインAPI)

認証が必要なAPIにアクセスする前に、まずはログインAPIでJWTトークンを取得する必要があります。

```bash
curl -X POST "http://localhost:8080/api/authentication/login" \
 -H "accept: application/json" \
 -H "Content-Type: application/json" \
 -d '{
  "email": "user@example.com",
  "password": "password"
}'
```

成功すると、以下のような形式でJWTトークンが返却されます：

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzEzMDE4MjAwLCJleHAiOjE3MTMwMjE4MDB9.xxxxxxxxxxxxxxxxxxxxxxxxxxxx"
}
```

失敗時（認証情報が間違っている場合など）は、401 Unauthorizedエラーが返却されます：

```json
{
  "timestamp": "...",
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication failed",
  "path": "/api/authentication/login"
}
```

### 2. トークンの使用方法

取得したJWTトークンを使って認証が必要なAPIにアクセスする方法は以下の通りです。

#### Swagger UIでのトークン設定

1. Swagger UI右上の "Authorize" ボタンをクリックします
2. 表示されたダイアログの "Value" フィールドに、取得したJWTトークンを `Bearer <token>` の形式で入力します
   ```
   Bearer eyJhbGciOiJIUzI1NiJ9...
   ```
3. "Authorize" ボタンをクリックしてダイアログを閉じます
4. これで、以降Swagger UIから送信するリクエストの `Authorization` ヘッダーにトークンが自動的に付与されます

#### curl/Postmanでのトークン使用

curlを使用する場合は、Authorizationヘッダーにトークンを設定します：

```bash
curl -X GET "http://localhost:8080/api/some-protected-endpoint" \
 -H "accept: application/json" \
 -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

Postmanを使用する場合は、Authorizationタブで「Bearer Token」を選択し、トークンを入力します。

## 主要API一覧

本プロジェクトで提供されている主要なAPIエンドポイントの一覧です。詳細な使用方法はSwagger UIを参照してください。

| エンドポイント | メソッド | 認証要否 | 説明 |
|--------------|---------|---------|-----|
| `/api/authentication/login` | POST | 不要 | メールアドレスとパスワードでログインし、JWT取得 |
| `/api/authentication/signup` | POST | 不要 | 新規ユーザー登録 |
| `/api/users/profile` | GET | 必要 | ログインユーザーのプロファイル情報取得 |
| `/api/users/profile` | PUT | 必要 | ログインユーザーのプロファイル情報更新 |

## トラブルシューティング

APIの操作中に問題が発生した場合は、以下を確認してください：

1. **認証関連**:
   - トークンの有効期限切れ → 再度ログインして新しいトークンを取得
   - トークン形式の誤り → `Bearer` の後にスペースを入れてトークンを設定

2. **リクエスト形式**:
   - Content-TypeがJSON形式になっているか確認 (`application/json`)
   - リクエストボディの形式が正しいか確認

3. **サーバー稼働状況**:
   - アプリケーションが起動しているか確認
   - 設定しているポート番号が正しいか確認

APIエラーが発生した場合は、サーバーのログを確認して詳細を調査してください。

## Postman

APIテスト用のGUIツールとしてPostmanの使用もおすすめします。現在、このプロジェクト用のPostmanコレクションは提供されていませんが、必要に応じて以下の手順で作成できます：

1. Postmanをインストール (https://www.postman.com/downloads/)
2. 新規コレクションを作成
3. 各APIエンドポイントをリクエストとして追加
4. 環境変数を設定（BaseURL、トークンなど）

これにより、APIのテストや操作が効率化されます。