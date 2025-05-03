# CLAUDE.md

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


作業に必要な資料は`.roo/rules`配下にあります。まずは
`.roo/rules/00_README.md`を確認してください。
基本的に `.roo/rules/`配下に前任者が作成したこのプロジェクトのメモ書きがありますので、まずはそれを確認してください。
また、`./docs/architect`配下にアーキテクチャの概要図が、`./docs/domain`にドメインモデルの詳細がありますので、必要に応じて確認してください。

信頼度は `./docs/architect`&`./docs/domain` > `src/` >`.roo/rules/` の順です。
基本的にはソースコードが正しく、ドキュメントは参考程度にしてください。
但し、`./docs/architect`&`./docs/domain`は概要設計者が作成したドキュメントを元に作成されていますので、信頼度は高いです。

作業の流れは以下の通りです。
1. 作業に必要なドキュメントを確認し、作業内容を理解する
2. 作業に必要な情報を収集する。この際、必要な情報はマネージャーに確認したり、githubのissueを確認したり、過去のコミットを確認したりしてください。
github は github mcp 経由でアクセスすることが可能です。また、brave mcpを使用することでブラウジングが可能です。
3. 作業を行う。いちいちマネージャーに確認する必要はありませんが、作業内容を理解してから作業を行ってください。
4. 作業が完了したら、作業内容に応じて、ドキュメントを更新してください。
5. 最後にgit commitを行ってください。コミットメッセージのルールは `.roo/rules/08_WORKFLOW.md` で確認してください。
