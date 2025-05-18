# GitHub Actions ワークフロー

このディレクトリには、プロジェクトのCI/CDワークフローが含まれています。

## ワークフロー構成

### 1. CI Pipeline (`ci.yml`)
- **トリガー**: 
  - mainブランチ以外へのプッシュ
  - PRの作成・更新
- **実行内容**:
  - コードフォーマットチェック（Spotless）
  - ビルド
  - 軽量テスト（Fastタグ）
  - API仕様に関連するPRの場合、Runnテストも実行
- **目的**: 開発中の素早いフィードバック

### 2. Merge Tests (`merge-tests.yml`)
- **トリガー**: 
  - mainブランチへのプッシュ
  - マージグループのチェック
- **実行内容**:
  - 全JUnitテスト（unit、integration、database、API）
  - Runn APIテスト
  - カバレッジレポート生成
- **目的**: 本番デプロイ前の完全な検証

### 3. Manual Tests (`manual-tests.yml`)
- **トリガー**: 手動実行
- **オプション**:
  - all: すべてのテスト
  - fast: 高速テストのみ
  - integration: 統合テスト
  - database: データベーステスト
  - api: APIテスト（JUnit）
  - runn: Runn APIテスト
  - custom: カスタムタグ指定
- **目的**: 特定のテストセットの手動実行

## テストタグ

- `@Fast`: 高速実行可能な単体テスト
- `@Slow`: 実行に時間がかかるテスト
- `@IntegrationTest`: システム統合テスト
- `@DatabaseTest`: データベース連携テスト
- `@ApiTest`: APIエンドポイントテスト

## Runn APIテスト

- `/test/api-tests/`配下のYAMLファイルで定義
- PR時は`[api]`タグまたは`api-test`ラベルで実行
- マージ時は自動実行

## ローカルでの実行方法

```bash
# 軽量テスト
./gradlew test -PincludeTags="Fast"

# すべてのテスト
./gradlew test

# Runn APIテスト
cd test
make test

# 特定のRunnテスト
make test-one FILE=api-tests/auth/login.yml
```

## セキュリティ設定

- JDK 23（GraalVM）を使用
- 依存関係のキャッシュでビルド高速化
- MongoDB 7をテスト用データベースとして使用
- テスト失敗時もレポートを必ずアップロード