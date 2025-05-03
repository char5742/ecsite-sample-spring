#!/bin/bash

# 出力ファイル
OUTPUT_FILE="CLAUDE.local.md"

# 元のCLAUDE.mdの内容を保持（基本情報部分のみ）
cat > $OUTPUT_FILE << 'EOL'
# CLAUDE.local.md

このファイルは、このリポジトリでコードを扱う際にClaude Code (claude.ai/code)に指針を提供します。

## ビルドコマンド
- ビルド: `./gradlew build`
- アプリケーション実行: `./gradlew bootRun`
- 全テスト実行: `./gradlew test`
- 単一テスト実行: `./gradlew test --tests "com.example.ec_2024b_back.auth.domain.models.AuthenticationTest"`
- タグ指定テスト実行: `./gradlew test -PincludeTags="Fast"` または `-PexcludeTags="Slow"`
- フォーマットチェック: `./gradlew spotlessCheck`
- フォーマット適用: `./gradlew spotlessApply`

## コードスタイルガイドライン
- アーキテクチャ: DDDの原則に基づくオニオンアーキテクチャ（ドメイン、アプリケーション、インフラストラクチャの各層）
- フォーマット: SpotlessによるGoogle Java Format（gitフックにより自動適用）
- 命名規則: クラスはパスカルケース、メソッド・変数はキャメルケース、パッケージはスネークケース
- テスト命名: `should[期待される結果]_when[テスト条件]`
- リアクティブプログラミングをReactor（Mono/Flux）で実装
- Null安全性: JSpecifyアノテーション（@Nullable）とNullAwayによる静的解析を使用
- エラー処理: ドメイン例外はDomainExceptionクラスを継承
- テスト: @Fast、@Slow、@IntegrationTest、@DatabaseTest、@ApiTestでテストにタグ付け

EOL

# マニュアルセクションの追加
echo "
# プロジェクトマニュアル
以下はプロジェクトのマニュアルを自動的にマージした内容です。元のドキュメントは .roo/rules ディレクトリに保存されています。
" >> $OUTPUT_FILE

# ファイルをマージする関数
merge_file() {
  local file=$1
  local filename=$(basename "$file")
  local title=${filename#*_}  # 数字とアンダースコアを削除
  title=${title%.md}          # .md拡張子を削除
  
  echo -e "\n## ${title}\n" >> $OUTPUT_FILE
  cat "$file" | sed '1,1d' >> $OUTPUT_FILE  # 1行目（タイトル）を削除
  echo -e "\n" >> $OUTPUT_FILE
}

# 指定ディレクトリ
RULES_DIR="./.roo/rules"

# メインのルールファイルをマージ（ファイル名順）
for file in $(ls $RULES_DIR/[0-9][0-9]_*.md | sort); do
  if [[ $(basename "$file") != "06_CODING_STANDARDS_AUTH.md" && $(basename "$file") != "99_CONTRIBUTING_TO_DOCS.md" ]]; then
    merge_file "$file"
  fi
done

# モジュール関連のファイルをマージ
echo -e "\n## モジュール詳細\n" >> $OUTPUT_FILE

# auth モジュール
if [ -f "$RULES_DIR/05_MODULES/auth/README.md" ]; then
  echo -e "\n### Auth モジュール\n" >> $OUTPUT_FILE
  cat "$RULES_DIR/05_MODULES/auth/README.md" | sed '1,1d' >> $OUTPUT_FILE
fi

# share モジュール
if [ -f "$RULES_DIR/05_MODULES/share/README.md" ]; then
  echo -e "\n### Share モジュール\n" >> $OUTPUT_FILE
  cat "$RULES_DIR/05_MODULES/share/README.md" | sed '1,1d' >> $OUTPUT_FILE
fi

# 追加情報を付加
echo "
# ヘルプとガイドライン
作業に必要な資料は\`.roo/rules\`配下にあります。基本的に \`.roo/rules/\`配下に前任者が作成したこのプロジェクトのメモ書きがありますので、まずはそれを確認してください。
また、\`./docs/architect\`配下にアーキテクチャの概要図が、\`./docs/domain\`にドメインモデルの詳細がありますので、必要に応じて確認してください。

信頼度は \`./docs/architect\`&\`./docs/domain\` > \`src/\` >\`.roo/rules/\` の順です。
基本的にはソースコードが正しく、ドキュメントは参考程度にしてください。
但し、\`./docs/architect\`&\`./docs/domain\`は概要設計者が作成したドキュメントを元に作成されていますので、信頼度は高いです。

作業の流れは以下の通りです。
1. 作業に必要なドキュメントを確認し、作業内容を理解する
2. 作業に必要な情報を収集する。この際、必要な情報はマネージャーに確認したり、githubのissueを確認したり、過去のコミットを確認したりしてください。
github は github mcp 経由でアクセスすることが可能です。また、brave mcpを使用することでブラウジングが可能です。
3. 作業を行う。いちいちマネージャーに確認する必要はありませんが、作業内容を理解してから作業を行ってください。
4. 作業が完了したら、作業内容に応じて、ドキュメントを更新してください。
5. 最後にgit commitを行ってください。コミットメッセージのルールは \`.roo/rules/08_WORKFLOW.md\` で確認してください。
" >> $OUTPUT_FILE

echo "マージが完了しました。結果は $OUTPUT_FILE に保存されています。"