# Auth モジュール

認証・認可機能とアカウント管理を担当します。

## 責務
- ユーザー認証（ログイン）
- アカウント作成（サインアップ）
- JWT管理

## 主要コンポーネント

### ドメインモデル
- `Account`: アカウント情報を表す集約ルート
- `Authentication`: 認証方法の共通インターフェース（現在は`EmailAuthentication`のみ）
- `JsonWebToken`: JWTを表す値オブジェクト

### リポジトリ
`Accounts`: アカウントの検索・保存（`findByEmail`, `save`）

### ワークフロー
- `LoginWorkflow`: ログイン処理
- `SignupWorkflow`: アカウント作成処理

## 主要フロー

### ログイン
1. メールアドレス・パスワード受信 → アカウント検索 → パスワード検証 → JWT生成

### サインアップ
1. メールアドレス・パスワード受信 → メールアドレス重複チェック → アカウント作成 → 保存

## 認証機構
- JWTを使用（`Authorization: Bearer <token>`）
- Spring Securityによる保護