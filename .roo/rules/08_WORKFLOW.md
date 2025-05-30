# 8. 開発ワークフローガイド

このドキュメントでは、`ecsite-v2` プロジェクトにおける標準的な開発ワークフローについて説明します。一貫したプロセスに従うことで、コードの品質を維持し、チームでの共同作業を円滑に進めることができます。

## Git Flow

当プロジェクトでは、Git Flowに基づいたブランチ戦略を採用しています。

### 主要ブランチ

| ブランチ | 説明 | 役割と制約 |
|---------|------|------------|
| `main` | リリース可能な安定版 | • 直接コミット禁止<br>• `release`または`hotfix`ブランチからのみマージ可能<br>• 常に本番環境にデプロイ可能な状態を維持 |
| `develop` | 開発統合用 | • 次期リリースに向けた開発の統合<br>• `feature`ブランチからのマージを受け入れ<br>• `main`ほど安定していないが、基本的な動作は保証 |

### サポートブランチ

| ブランチ | 発生元 | マージ先 | 命名規則 | 用途 |
|---------|-------|---------|---------|-------|
| `feature/*` | `develop` | `develop` | `feature/issue-<番号>-<説明>` | 新機能開発、既存機能改善、リファクタリングなど |
| `release/*` | `develop` | `main` + `develop` | `release/v<バージョン>` | リリース前の最終調整、軽微なバグ修正、ドキュメント更新 |
| `hotfix/*` | `main` | `main` + `develop` | `hotfix/<説明>` | 本番環境の緊急バグ修正 |

### 基本的な開発フロー (新機能開発)

1. **最新のdevelopブランチを取得:**
   ```bash
   git checkout develop
   git pull origin develop
   ```

2. **featureブランチを作成:**
   ```bash
   git checkout -b feature/issue-XXX-your-feature-name develop
   ```

3. **コーディングとコミット:**
   * 機能開発を進め、適切な粒度でコミット
   * コミットメッセージは後述の規約に従う
   ```bash
   git add .
   git commit -m "feat: 🎸 ユーザープロファイル編集機能を追加"
   ```

4. **リモートへのプッシュ:**
   ```bash
   git push origin feature/issue-XXX-your-feature-name
   ```

5. **Pull Request (PR) の作成:**
   * GitHubで`feature/issue-XXX`から`develop`へのPRを作成
   * テンプレートに従い、変更内容、理由、テスト内容などを記述

6. **コードレビュー:**
   * レビュワーを指名し、レビューを依頼
   * レビューでの指摘事項に対応し、必要に応じて追加コミット

7. **PRのマージ:**
   * レビュー承認後、`develop`ブランチにマージ
   * 通常はSquash and mergeは使用せず、履歴を保持

8. **ローカルブランチの削除 (任意):**
   ```bash
   git checkout develop
   git branch -d feature/issue-XXX-your-feature-name
   ```

## コミット前の自動チェック (Lefthook)

当プロジェクトでは、コミット前にコードの品質を自動的にチェック・整形するために [Lefthook](https://github.com/evilmartians/lefthook) を使用しています。

* **設定ファイル:** プロジェクトルートの `lefthook.yml`
* **pre-commitフック:** コミット前に以下の処理が自動実行
  * **Javaコード整形:** Spotless Gradleプラグイン (`./gradlew spotlessApply`) によるフォーマット
* **動作:** `git commit`実行時に自動的に実行され、整形によりファイルが変更された場合は再度ステージングが必要
  ```bash
  # 整形によりファイルが変更された場合
  git add .
  git commit --amend --no-edit  # または再度コミットメッセージを入力
  ```
* **スキップ (非推奨):** 一時的にフックをスキップする場合は `--no-verify` オプションを使用
  ```bash
  git commit -m "..." --no-verify
  ```

## コミットメッセージ規約

当プロジェクトでは、以下の形式に基づいたコミットメッセージを使用します。この規約は効果的な履歴管理とリリースノート自動生成のために重要です。

**フォーマット:**
```
{type}: {emoji}{subject}

[optional body]
```

### タイプと絵文字

| タイプ | 説明 | 絵文字 | 使用例 |
|--------|------|--------|--------|
| `feat` | ユーザー向けの新機能実装 | 🎸 | `feat: 🎸 パスワードリセット機能を追加` |
| `fix` | バグ修正 | 🐛 | `fix: 🐛 ログイン時のNullPointerExceptionを修正` |
| `docs` | ドキュメント更新 | ✏️ | `docs: ✏️ READMEのセットアップ手順を更新` |
| `style` | コードスタイル変更 (機能への影響なし) | 💄 | `style: 💄 インデントを修正` |
| `refactor` | 機能変更のないコード改善 | 💡 | `refactor: 💡 認証処理をリファクタリング` |
| `perf` | パフォーマンス改善 | ⚡️ | `perf: ⚡️ データベースクエリを最適化` |
| `test` | テスト追加・修正 | 💍 | `test: 💍 ユーザー登録APIのテストケースを追加` |
| `build` | ビルドシステム・依存関係変更 | 🏗️ | `build: 🏗️ Spring Bootを3.1.2に更新` |
| `ci` | CI設定変更 | 🎡 | `ci: 🎡 GitHub Actionsのワークフローを改善` |
| `chore` | その他の変更 | 🤖 | `chore: 🤖 .gitignoreを更新` |
| `release` | リリース関連 | 🏹 | `release: 🏹 v1.0.0をリリース` |

### コミットメッセージの詳細

* **タイプ (type):** 変更の種類を示すキーワード (必須)。上表のいずれかを使用。
* **絵文字 (emoji):** 変更の種類を視覚的に示す (必須)。上表のいずれかを使用。
* **件名 (subject):** 変更内容の簡潔な説明 (必須)
  * 64文字以内を推奨
  * 現在形・命令形で記述
  * 大文字で始めず、ピリオドで終えない
* **本文 (body):** 変更の詳細な説明 (任意)
  * 変更の理由、背景、以前の動作との違いなど
  * 空行を挟んで記述

### コミットメッセージ例

```
feat: 🎸 ユーザープロファイル編集機能を追加

- プロフィール更新用のAPIエンドポイントを実装
- 関連するサービスとリポジトリロジックを追加
- ユーザーモジュールのドキュメントを更新

Refs #123
```

```
fix: 🐛 ログイン時のNullPointerExceptionを修正

特定の条件下でアカウント情報が取得できずにエラーが発生していた問題を修正。
リポジトリ層でのOptionalの扱いを見直し。
```

## Pull Request (PR)

PRは、コード変更をレビューし、`develop`や`main`ブランチにマージするための主要な手段です。

### PRテンプレート項目

* **変更の概要:** 何を変更したのか簡潔に
* **変更の理由/目的:** 背景や解決しようとした問題
* **関連Issue:** 関連するIssueへのリンク (`Fixes #XXX`, `Refs #XXX`)
* **変更内容の詳細:** 実装方法や技術的な判断など
* **テスト内容:** 単体テスト、結合テスト、手動確認など
* **レビュー依頼事項:** 特に見てほしい点、懸念点など
* **スクリーンショット/動画:** UI変更がある場合に添付

### WIP (Work In Progress) PR

開発途中のコードで早期フィードバックが欲しい場合に使用:
* タイトルに`[WIP]`をつけてPRを作成
* マージ準備が整うまでマージ不可のまま議論を進める
* 準備が整ったら`[WIP]`を外し、最終レビューを依頼

## コードレビュー

### レビュー観点

* **設計:** 要件充足、拡張性、保守性、DDDの原則適用
* **ロジック:** 動作正確性、エッジケース対応、リアクティブパターン適用
* **可読性:** 理解しやすさ、命名適切性、コメントの質
* **テスト:** カバレッジ、テスト品質、単体/結合テストの適切な使い分け
* **規約:** コーディング規約準拠、プロジェクト標準の適用
* **パフォーマンス:** 効率性、リソース使用、リアクティブストリーム適用
* **セキュリティ:** 認証・認可の取り扱い、入力検証、トークン管理

### レビュープロセス

1. PRに割り当てられたらできるだけ2営業日以内にレビューする
2. 具体的かつ建設的なコメントを心がける（単なる問題点指摘ではなく改善案も）
3. 敬意を持ったコミュニケーションを取る
4. LGTM (Looks Good To Me) だけでなく、良いと思った理由も伝える

### レビュー後のフロー

1. 指摘事項に対応し、必要に応じて追加コミット
2. 変更点を説明するコメントを残す
3. PRのマージには、少なくとも1人のチームメンバーの承認が必要

## CI/CD

現在、GitHub Actionsなどを用いたCI/CDパイプラインは設定されていませんが、将来的には以下の導入を検討しています:

* **CI:** PR作成時やブランチへのプッシュ時の自動ビルド・テスト実行
* **静的解析:** Spotless, NullAway, Error Proneによるコード品質チェック
* **テスト実行:** JUnit, Testcontainersを使った自動テスト
* **CD:** 承認済みPRのdevelopブランチへのマージ後、開発環境への自動デプロイ