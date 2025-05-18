# GitHub Actions ワークフロー

このディレクトリには、このプロジェクトのCI/CDワークフローが含まれています。

## ワークフロー一覧

### 1. CI (Light Tests) (`ci.yml`)
- **トリガー**: 
  - mainブランチ以外へのプッシュ
  - mainブランチへのプルリクエスト
- **実行内容**:
  - コードフォーマットチェック（Spotless）
  - プロジェクトのビルド（テストなし）
  - 軽量テスト（Fastタグ）のみ実行
  - 失敗時のレポートアップロード
- **目的**: 開発中の素早いフィードバック

### 2. Merge Tests (Heavy) (`merge-tests.yml`)
- **トリガー**: 
  - mainブランチへのプッシュ（マージ時）
  - マージグループのチェック要求
- **実行内容**:
  - コードフォーマットチェック（Spotless）
  - プロジェクトのビルド
  - 全テストの実行
  - 統合テスト、データベーステスト、APIテストの実行
  - テストカバレッジレポートの生成
  - 全レポートのアップロード
- **目的**: マージ前の完全な検証

### 3. タグ付きテスト (`test-with-tags.yml`)
- **トリガー**: 手動実行（workflow_dispatch）
- **パラメータ**:
  - `include_tags`: 実行するテストタグ（カンマ区切り）
  - `exclude_tags`: 除外するテストタグ（カンマ区切り）
- **使用例**:
  - Fastタグのみ実行: `include_tags: Fast`
  - Slowタグを除外: `exclude_tags: Slow`
  - 複数タグの組み合わせ: `include_tags: Fast,IntegrationTest`

### 4. カテゴリ別テスト (`test-categories.yml`)
- **トリガー**: 
  - mainブランチへのプルリクエスト
  - 手動実行
- **ジョブ**:
  - `test-fast`: Fastタグのテストのみ実行
  - `test-integration`: IntegrationTestタグのテストのみ実行
  - `test-without-slow`: Slowタグ以外のテストを実行

## テスト戦略

### 開発時（コミット/PR時）
- 軽量テスト（Fastタグ）のみを実行
- 素早いフィードバックループを提供
- フォーマットとビルドの確認

### マージ時
- 全テストを実行
- 統合テスト、データベーステスト、APIテストを含む
- カバレッジレポートの生成
- 完全な品質保証

## 使用されているテストタグ

プロジェクトで定義されているテストタグ：
- `@Fast`: 高速に実行されるユニットテスト（開発時に実行）
- `@Slow`: 実行に時間がかかるテスト（マージ時に実行）
- `@IntegrationTest`: 統合テスト（マージ時に実行）
- `@DatabaseTest`: データベースを使用するテスト（マージ時に実行）
- `@ApiTest`: APIテスト（マージ時に実行）

## キャッシュ戦略

全てのワークフローで以下のキャッシュが設定されています：
- Gradleの依存関係キャッシュ
- Gradleラッパーのキャッシュ

キャッシュキーはGradleファイルのハッシュ値に基づいて生成されます。

## セキュリティとベストプラクティス

1. JDK 17（Temurin）を使用
2. 最新のGitHub Actionsを使用（actions/checkout@v4など）
3. gradlewに実行権限を付与してから実行
4. テスト失敗時もレポートをアップロード（`if: always()`）
5. Pull RequestではFastテストを先に実行し、フィードバックを素早く提供

## ローカルでのテスト実行

ワークフローで実行されるコマンドは、ローカルでも同様に実行できます：

```bash
# フォーマットチェック
./gradlew spotlessCheck

# ビルド（テストなし）
./gradlew build -x test

# 軽量テスト（開発時）
./gradlew test -PincludeTags="Fast"

# 全テスト実行（マージ前）
./gradlew test

# 特定カテゴリのテスト
./gradlew test -PincludeTags="IntegrationTest"
./gradlew test -PincludeTags="DatabaseTest"
./gradlew test -PincludeTags="ApiTest"

# 重いテストを除外
./gradlew test -PexcludeTags="Slow"
```