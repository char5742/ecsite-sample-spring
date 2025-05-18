#!/usr/bin/env bash
set -euo pipefail

# 1. ステージ済み変更のチェック
if git diff --cached --quiet; then
  echo "⚠️  ステージ済みの変更がありません。まず git add してください。"
  exit 1
fi

# 2. diff を制限付きで取得
diff_text=$(git diff --cached | head -c 15000)

# 3. Claude に渡すプロンプト
prompt=$(cat <<'EOF'
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

- 特定の条件下でアカウント情報が取得できずにエラーが発生していた問題を修正
- リポジトリ層でのOptionalの扱いを見直し
```

以下の Git diff から、規約にそってコミットメッセージを提案してください。
そのままcommit するので、コミットメッセージのみを出力してください。
EOF
)

# 4. Claude に送り込む
generated_msg=$(
  { printf "%s\n\n%s\n" "$prompt" "$diff_text"; } \
  | claude -p --print --output-format text
)

# 5. ユーザー確認
echo -e "\n📝 Claude の提案: \n\033[1m${generated_msg}\033[0m"
read -rp "↑ このメッセージでコミットしますか？ [y/N]: " answer
if [[ $answer =~ ^[Yy]$ ]]; then
  git commit -m "$generated_msg"
  echo "✅ コミットしました！"
else
  echo "キャンセルしました。メッセージはクリップボードに残しておくね🫶"
  command -v pbcopy &>/dev/null && printf "%s" "$generated_msg" | pbcopy
fi
