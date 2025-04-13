# 11. API利用ガイド

このドキュメントでは、`ecsite-v2` プロジェクトが提供するAPIの利用方法について説明します。主に開発中の動作確認やテストを目的としています。

*(現在執筆中です)*

## Swagger UI

*   **概要:** 本プロジェクトでは [SpringDoc](https://springdoc.org/) を利用しており、アプリケーション起動時に OpenAPI 仕様に基づいたインタラクティブな API ドキュメント (Swagger UI) が自動的に生成・提供されます。API の仕様確認や簡単な動作テストに利用できます。
*   **アクセスURL:** アプリケーションをローカルで起動後、Webブラウザで `http://localhost:8080/swagger-ui.html` にアクセスしてください。(ポート番号は環境によって異なる場合があります)
*   **使い方:**
    *   画面上部に API のリストが表示されます。各 API をクリックすると詳細（説明、パラメータ、リクエストボディ、レスポンススキーマ、試行機能）が展開されます。
    *   **API の試行:**
        1.  試したい API を展開します。
        2.  右上の "Try it out" ボタンをクリックします。
        3.  必要なパラメータやリクエストボディを入力します。リクエストボディのスキーマ (例) も表示されるため参考にしてください。
        4.  "Execute" ボタンをクリックするとリクエストが送信され、結果 (レスポンスコード、レスポンスボディ、ヘッダー、curl コマンド例など) が表示されます。
*   **注意点:**
    *   API 定義は `docs/openapi/entry.yml` から OpenAPI Generator によって生成されたコードに基づいています。
    *   認証が必要な API を試す場合は、後述の「認証」セクションを参照して認証情報を設定する必要があります。

## 認証

多くの API は認証 (ログイン) が必要です。認証には JWT (JSON Web Token) を使用します。

1.  **トークン取得:**
    *   まず、`/api/authentication/login` エンドポイント (Swagger UI または curl/Postman などで) を使用してログインし、レスポンスに含まれる JWT トークンを取得します。
2.  **Swagger UI でのトークン設定:**
    *   Swagger UI 右上の "Authorize" ボタンをクリックします。
    *   表示されたダイアログの "Value" フィールドに、取得した JWT トークンを `Bearer <token>` の形式で入力します (例: `Bearer eyJhbGciOiJIUzI1NiJ9...`)。
    *   "Authorize" ボタンをクリックし、ダイアログを閉じます。
    *   これで、以降 Swagger UI から送信されるリクエストの `Authorization` ヘッダーにトークンが付与されます。
3.  **curl/Postman でのトークン設定:**
    *   リクエストヘッダーに `Authorization: Bearer <token>` を追加します。

*(認証が不要な公開 API (例: ログイン、商品一覧など) についても明記予定)*

## 主要API利用例

*(アカウント登録、ログイン、商品検索、注文など、代表的なAPIについて `curl` や Postman でのリクエスト/レスポンス例を記載予定)*

### 例: 商品一覧取得 (curl)

```bash
curl -X GET "http://localhost:8080/api/products?page=0&size=10" -H "accept: application/json"
```

### 例: アカウント登録 (curl)

```bash
curl -X POST "http://localhost:8080/api/accounts" \
 -H "accept: application/json" \
 -H "Content-Type: application/json" \
 -d '{ "email": "test@example.com", "password": "password123", "name": "Test User" }'
```

## Postman

*   **概要:** API開発・テストのための高機能なツールです。
*   **コレクション:** (もし提供する場合) プロジェクトのAPIリクエストをまとめたPostmanコレクションファイル (`*.postman_collection.json`) のインポート方法を記載します。
*   **環境変数:** (もし提供する場合) 認証トークンやベースURLなどを管理するためのPostman環境ファイル (`*.postman_environment.json`) の設定方法を記載します。

*(Postmanの基本的な使い方、コレクション/環境の活用方法などを記載予定)*
