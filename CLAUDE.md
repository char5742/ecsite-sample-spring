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

