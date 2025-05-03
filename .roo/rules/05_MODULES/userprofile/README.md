# UserProfile モジュール

ユーザープロファイルと住所情報を管理します。

## 責務
- ユーザー基本情報管理（名前など）
- アカウント (`auth`) との紐付け
- 複数配送先住所の管理

## 主要コンポーネント

### ドメインモデル
- `UserProfile`: 集約ルート。ユーザー情報と住所リストを保持
- `Address`: 住所情報の値オブジェクト

### リポジトリ
`UserProfiles`: 検索・保存操作を定義（`findById`, `findByAccountId`, `save`）

### ワークフロー
- `CreateUserProfileWorkflow`: プロファイル作成
- `AddAddressWorkflow`: 住所追加
- `UpdateUserProfileWorkflow`: プロファイル更新
- `RemoveAddressWorkflow`: 住所削除

## 依存関係
- `auth`: アカウントIDを利用
- `share`: 共通コンポーネント利用

## データモデル
`user_profiles`コレクションに格納（[詳細](../09_DATABASE.md)）