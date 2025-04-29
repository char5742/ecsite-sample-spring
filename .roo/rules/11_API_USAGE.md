# 11. API利用ガイド

このドキュメントでは、`ecsite-v2` プロジェクトが提供するAPIの利用方法について説明します。主に開発中の動作確認やテストを目的としています。

## Swagger UI

*   **概要:** 本プロジェクトでは [SpringDoc](https://springdoc.org/) を利用しており、OpenAPI 仕様に基づいたインタラクティブな API ドキュメント (Swagger UI) を生成する機能が含まれています。
*   **有効化:** デフォルトでは OpenAPI Generator の設定 (`openapi.gradle` の `useSwaggerUI: "false"`) により、Swagger UI は無効化されています。利用したい場合は、`src/main/resources/application.properties` に以下の設定を追加することで有効化できます。
    ```properties
    springdoc.swagger-ui.enabled=true
    ```
*   **アクセスURL (有効化した場合):** アプリケーションをローカルで起動後、Webブラウザで `http://localhost:8080/swagger-ui.html` にアクセスしてください。(ポート番号は環境によって異なる場合があります)
*   **使い方 (有効化した場合):**
    *   画面上部に API のリストが表示されます。各 API をクリックすると詳細（説明、パラメータ、リクエストボディ、レスポンススキーマ、試行機能）が展開されます。
    *   **API の試行:**
        1.  試したい API を展開します。
        2.  右上の "Try it out" ボタンをクリックします。
        3.  必要なパラメータやリクエストボディを入力します。リクエストボディのスキーマ (例) も表示されるため参考にしてください。
        4.  "Execute" ボタンをクリックするとリクエストが送信され、結果 (レスポンスコード、レスポンスボディ、ヘッダー、curl コマンド例など) が表示されます。
*   **注意点:**
    *   認証が必要な API を試す場合は、後述の「認証」セクションを参照して認証情報を設定する必要があります。

## 認証

多くの API は認証 (ログイン) が必要です。認証には JWT (JSON Web Token) を使用します。

1.  **トークン取得 (ログイン API):**
    *   まず、`POST /api/authentication/login` エンドポイントを使用してログインします。リクエストボディには `email` と `password` を含めます。
    *   認証に成功すると、レスポンスボディに JWT トークンが含まれます (`LoginResponse` スキーマ参照)。
    *   このエンドポイントは認証が不要です。
2.  **Swagger UI でのトークン設定 (有効化した場合):**
    *   Swagger UI 右上の "Authorize" ボタンをクリックします。
    *   表示されたダイアログの "Value" フィールドに、取得した JWT トークンを `Bearer <token>` の形式で入力します (例: `Bearer eyJhbGciOiJIUzI1NiJ9...`)。
    *   "Authorize" ボタンをクリックし、ダイアログを閉じます。
    *   これで、以降 Swagger UI から送信されるリクエストの `Authorization` ヘッダーにトークンが付与されます。
3.  **curl/Postman でのトークン設定:**
    *   リクエストヘッダーに `Authorization: Bearer <token>` を追加します。

## 主要API利用例

現在実装されている主要な API の利用例を以下に示します。

### 例: ログイン (curl)

```bash
curl -X POST "http://localhost:8080/api/authentication/login" \
 -H "accept: application/json" \
 -H "Content-Type: application/json" \
 -d '{
  "email": "user@example.com",
  "password": "password"
}'

# 成功時のレスポンス例 (ステータスコード 200)
# {
#  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzEzMDE4MjAwLCJleHAiOjE3MTMwMjE4MDB9.xxxxxxxxxxxxxxxxxxxxxxxxxxxx"
# }

# 失敗時のレスポンス例 (ステータスコード 401)
# {
#  "timestamp": "...",
#  "status": 401,
#  "error": "Unauthorized",
#  "message": "Authentication failed", # 実際のエラーメッセージは異なる場合があります
#  "path": "/api/authentication/login"
# }
```

*(注意: サインアップ API は現在 OpenAPI 定義に含まれていません。実装されている場合は `SignupUsecase` を参照してください)*

## Postman

*   **概要:** API開発・テストのための高機能なツールです。
*   **コレクション/環境:** 現在、このプロジェクト用の Postman コレクションや環境ファイルは提供されていません。必要に応じて各自で作成してください。
