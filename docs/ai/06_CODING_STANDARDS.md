# 6. コーディング規約ガイド

このドキュメントでは、`ecsite-v2` プロジェクトにおけるコーディング規約とベストプラクティスについて説明します。一貫性のあるコードは、可読性、保守性を高め、チーム開発を円滑にします。

*(現在執筆中です)*

## 基本原則

*(可読性、保守性、一貫性などの基本方針を記載予定)*

## 命名規則

*(パッケージ、クラス、メソッド、変数などの命名規則を記載予定)*

## フォーマット

*   当プロジェクトでは [Spotless](https://github.com/diffplug/spotless) を使用してコードフォーマットを自動的に適用します。
*   設定ファイル: `spotless.gradle`
*   適用コマンド: `./gradlew spotlessApply`
*   チェックコマンド: `./gradlew spotlessCheck`
*   コミット前に `spotlessApply` を実行し、コードスタイルを統一してください。
*(具体的なフォーマットルール (インデント、括弧の位置など) の詳細を記載予定)*

## コメント

*(Javadocの書き方、コメントすべき箇所、避けるべきコメントなどを記載予定)*

## Java コーディング規約

*(`var` の使用基準, Stream API, Optional, 例外処理, Lombok のガイドラインなどを記載予定)*

## 設定値の取得（Properties）

本プロジェクトでは、設定値（`application.properties`に記述される値）の取得方法として、以下のガイドラインに従ってください：

*   **推奨: Propertiesクラスを使用:**
    *   設定値は直接`@Value`アノテーションで取得するのではなく、専用のPropertiesクラスに集約します。
    *   例:
      ```java
      @Validated
      @Data
      @Configuration("jwt")
      public class JWTProperties {
        /** JWTのシークレットキー */
        @NotNull private String secret;

        /** JWTの有効期限（ミリ秒） */
        @NotNull private Long expirationMillis;
      }
      ```
    *   利用側では、このPropertiesクラスをDIして使用します:
      ```java
      @Component
      @RequiredArgsConstructor
      public class JsonWebTokenProvider {
        private final JWTProperties jwtProperties;
        
        // jwtProperties.getSecret() などで値を取得
      }
      ```

*   **理由:**
    *   型安全性の向上: 設定値の型が明示的に定義されます
    *   テスト容易性: モックやスタブの作成が容易になります
    *   設定のグループ化: 関連する設定値がクラスにまとめられることで可読性が向上します
    *   バリデーション: `@Validated`アノテーションにより、起動時に設定値の検証が可能です
    *   IDE補完: フィールド名の補完が利用できるため、タイプミスを減らせます

*   **避けるべき方法:**
    ```java
    @Value("${jwt.secret}")
    private String secret; // これは避ける
    ```

## 関数型プログラミング (Vavr) 規約

本プロジェクトでは、コードの堅牢性、テスト容易性、表現力を高めるために、関数型プログラミングの原則と [Vavr](https://www.vavr.io/) ライブラリの活用を推奨します。特に Domain 層と Application 層での積極的な利用を検討してください。

*   **不変性 (Immutability):**
    *   可能な限り不変なデータ構造を使用します。Vavr のイミュータブルコレクション (`List`, `Map`, `Set` など) の利用を推奨します。
    *   ドメインオブジェクトやDTOも可能な限りイミュータブルに設計します (Lombok の `@Value` など)。
*   **副作用の分離 (Side Effect Isolation):**
    *   副作用（状態の変更、I/O操作など）を持つ処理は、純粋な関数から分離します。
    *   `Try`, `Either`, `Option` を使用して、エラーや欠損値を明示的に扱います。
*   **エラーハンドリング (`Try`, `Either`):**
    *   例外をスローする代わりに、`Try` や `Either` を使用して処理の成功/失敗を表現します。これにより、呼び出し側でエラー処理を強制し、より安全なコードになります。
    *   `Try`: 例外が発生する可能性のある処理をラップします。
    *   `Either`: 成功時の値 (Right) または失敗時の値/エラー情報 (Left) のいずれかを持つことを表現します。ドメインエラーなど、特定の失敗ケースを型で表現したい場合に有効です。
*   **値の欠損 (`Option`):**
    *   `null` を返す代わりに `Option` (`Some` または `None`) を使用します。これにより、NullPointerException を防ぎ、値が存在しないケースを明示的に扱えます。
    *   Java 標準の `Optional` よりも Vavr の `Option` の方が、より豊富な API を提供するため推奨されます。
*   **関数合成 (Function Composition):**
    *   `andThen()` や `compose()` を利用して、小さな関数を組み合わせて複雑な処理を構築します。
    *   Domain 層の Workflow/Step パターンでは、この関数合成を積極的に活用します。
*   **パターンマッチング (Pattern Matching):**
    *   `Match` API を使用して、複雑な `if-else` や `switch` 文をより宣言的かつ安全に記述します。`Try`, `Either`, `Option` などの結果を処理する際に特に有効です。
*   **Vavr コレクション:**
    *   Java 標準のコレクションよりも関数型操作が豊富な Vavr のイミュータブルコレクション (`io.vavr.collection.*`) の利用を検討します。

これらの規約を適用することで、より宣言的で、予測可能で、テストしやすいコードを目指します。

## リアクティブプログラミング規約

*(パイプラインの書き方, `subscribe()` の位置, エラーハンドリングなどを記載予定)*

## ログ規約

*(ログレベルの使い分け, メッセージフォーマットなどを記載予定)*

## その他

*(マジックナンバー禁止, DRY原則など、その他の規約を記載予定)*
