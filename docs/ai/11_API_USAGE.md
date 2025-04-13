# 11. API利用ガイド

このドキュメントでは、`ecsite-v2` プロジェクトが提供するAPIの利用方法について説明します。主に開発中の動作確認やテストを目的としています。

*(現在執筆中です)*

## Swagger UI

*   **概要:** SpringDoc OpenAPI によって自動生成されるAPIドキュメント兼テストツールです。
*   **アクセスURL:** アプリケーション起動後、通常は `http://localhost:8080/swagger-ui.html` でアクセスできます。
*   **使い方:**
    *   利用可能なAPIエンドポイントの一覧表示。
    *   各エンドポイントの詳細（パラメータ、リクエストボディ、レスポンススキーマ）の確認。
    *   画面上からのAPIリクエスト試行。
*(Swagger UIの具体的な操作方法、認証情報の入力方法などを記載予定)*

## 認証

*(認証が必要なAPIと不要なAPIの区別、認証トークン (JWTなど) の取得方法、Swagger UIやcurl/Postmanでのトークン設定方法などを記載予定)*

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
