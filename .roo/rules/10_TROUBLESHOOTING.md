# 10. トラブルシューティングガイド

このドキュメントでは、開発中に遭遇する可能性のある一般的な問題とその解決策、および問題発生時の調査方法について説明します。

## 問題切り分けの基本手順

問題が発生した場合、以下の手順で切り分けることが有効です。

1. **エラーメッセージ確認:**
   * コンソールやログに出力されたエラーメッセージを正確に読む
   * 特に例外の種類 (`NullPointerException`, `IllegalArgumentException` など) とスタックトレースに注目

2. **スタックトレース解析:**
   * エラーが発生するまでのメソッド呼び出し履歴を示す
   * 一番上の行が直接的なエラー発生箇所
   * 呼び出し元を辿ることで、エラーの原因となったコード箇所を特定

3. **再現手順特定:**
   * どのような操作や条件で問題が必ず発生するかを特定
   * これによりデバッグや原因調査が効率化される

4. **ログレベル変更:**
   * 必要に応じてログレベルを `DEBUG` や `TRACE` に変更して詳細情報を取得
   * `application.properties` の `logging.level.*` プロパティで設定
   * 詳細は [03_BUILD_RUN_DEBUG.md](./03_BUILD_RUN_DEBUG.md) を参照

5. **デバッガの利用:**
   * デバッガを使ってコードをステップ実行し、変数の値や処理の流れを確認
   * 詳細は [03_BUILD_RUN_DEBUG.md](./03_BUILD_RUN_DEBUG.md) を参照

## よくある問題と解決策

### 環境構築時のエラー

| 問題 | 原因 | 解決策 |
|------|------|---------|
| **JDK関連エラー** | • JDKバージョン不一致 (JDK 23 以外の使用)<br>• `JAVA_HOME` が未設定または誤っている<br>• IDEのJDK設定が誤っている | • [02_ENVIRONMENT_SETUP.md](./02_ENVIRONMENT_SETUP.md) に従い、正しい JDK をインストール<br>• 環境変数を正しく設定<br>• IDEの設定を確認 |
| **Docker関連エラー** | • Dockerが未起動<br>• MongoDBコンテナ起動失敗<br>• ポート競合 (27017ポート)<br>• リソース不足 | • Docker Desktop起動<br>• `docker ps`でコンテナ確認<br>• `docker logs local-mongo`でログ確認<br>• 別ポートで再起動 (`docker run ... -p <別ポート>:27017 ...`)<br>• Docker設定でリソース割り当て確認 |
| **Gradle関連エラー** | • 依存関係ダウンロード失敗<br>• キャッシュ破損<br>• ネットワーク制限<br>• Gradle Wrapperの破損 | • ネットワーク接続確認<br>• `./gradlew clean build --refresh-dependencies`<br>• プロキシ設定確認<br>• 必要に応じてGradle Wrapperを再生成 |

### ビルド/コンパイルエラー

| 問題 | 原因 | 解決策 |
|------|------|---------|
| **コンパイルエラー** | • 文法ミス<br>• 型不一致<br>• import忘れ<br>• 依存関係不足 | • IDEのエラー表示確認<br>• コンパイラメッセージに従い修正<br>• 必要なライブラリを`build.gradle`に追加 |
| **NullAwayエラー** | • `@Nullable`でない変数に`null`が代入される可能性<br>• `@Nullable`な変数をチェックせずに使用<br>• `@NullMarked`/`@NullUnmarked`の使い分け誤り | • [06_CODING_STANDARDS.md](./06_CODING_STANDARDS.md) のNullAwayセクション参照<br>• 適切な`@Nullable`アノテーション適用<br>• nullチェック追加<br>• `Optional`活用 |
| **テスト失敗** | • コード変更によるテスト前提条件の崩れ<br>• アサーションエラー<br>• テスト内の実行時例外<br>• モックの設定ミス | • `build/reports/tests/test/index.html`でテスト結果確認<br>• テストコードまたは本体コードを修正<br>• モックの振る舞いを正しく設定 |

### 実行時エラー

| 問題 | 原因 | 解決策 |
|------|------|---------|
| **NullPointerException** | • nullオブジェクトのメソッド/フィールドアクセス<br>• NullAwayが検出できないケース | • スタックトレースで発生箇所特定<br>• nullチェック追加<br>• `Optional`活用<br>• 防御的プログラミングの適用 |
| **ClassCastException** | • 互換性のない型へのキャスト<br>• 型検査の欠如 | • キャスト前に`instanceof`で型検査<br>• コードロジック見直し<br>• ジェネリクス活用 |
| **MongoDB接続エラー** | • コンテナ未起動<br>• 接続設定不正<br>• ネットワーク問題 | • コンテナ起動確認<br>• `application.properties`の`spring.data.mongodb.uri`確認<br>• ネットワーク接続確認 |
| **リアクティブ関連エラー** | • デッドロック<br>• バックプレッシャー問題<br>• リソースリーク<br>• ブロッキング処理 | • Reactorの`log()`オペレータ使用<br>• デバッガで処理フロー確認<br>• ブロッキング処理排除<br>• `subscribeOn`/`publishOn`の適切な使用 |

### APIエラー

| ステータス | 原因 | 解決策 |
|----------|------|---------|
| **4xx (クライアントエラー)** | • リクエスト形式不正<br>• 必須パラメータ欠落<br>• 型違い<br>• トークン無効/未提供 | • API仕様 (Swagger UI) 確認<br>• リクエスト内容修正<br>• 認証トークン確認<br>• デバッグログでリクエスト内容検証 |
| **5xx (サーバーエラー)** | • サーバー内部例外<br>• 未処理例外のスロー<br>• DB接続問題<br>• リソース枯渇 | • サーバーログ確認<br>• スタックトレース解析<br>• 例外処理の追加<br>• エラーハンドリング改善 |

## 特定シナリオの対処方法

### MongoDBインデックス問題

MongoDBの検索パフォーマンスが遅い場合：

1. **問題確認:**
   ```bash
   # コンテナ内のMongoシェル起動
   docker exec -it local-mongo mongosh
   
   # 使用中のDB選択
   use test
   
   # クエリ実行計画確認
   db.accounts.find({email: "user@example.com"}).explain("executionStats")
   ```

2. **インデックス作成:**
   ```javascript
   // メールアドレスでのインデックス作成例
   db.accounts.createIndex({"authentications.email": 1}, {unique: true})
   ```

### リアクティブストリームのデバッグ

リアクティブプログラミングのデバッグには以下のテクニックが有効です：

1. **`log()`オペレータの挿入:**
   ```java
   return accountRepository.findByEmail(email)
       .log("FIND_RESULT") // 処理の各ステップでシグナルをログ出力
       .flatMap(this::processAccount)
       .log("PROCESS_RESULT");
   ```

2. **デバッグレベルの有効化:**
   ```properties
   # application.properties
   logging.level.reactor.core.publisher.Operators=DEBUG
   logging.level.reactor=DEBUG
   ```

3. **ブロッキング呼び出しの特定:**
   BlockHoundなどのツールを使用（現在未導入）

## 調査ツール

### ログ確認

* **コンソールログ:** デフォルトではコンソールに出力されます
* **ファイルログ設定:**
  ```properties
  # application.properties
  logging.file.name=application.log
  logging.file.path=/path/to/logs
  ```
* **ログレベル変更:**
  ```properties
  # 全体のログレベル
  logging.level.root=INFO
  
  # 特定パッケージのログレベル
  logging.level.com.example.ec_2024b_back=DEBUG
  logging.level.com.example.ec_2024b_back.auth=TRACE
  ```

### デバッガ活用

* **IDEデバッガ:** IntelliJ IDEAなどのJavaデバッガを使用
* **リモートデバッグ:**
  ```bash
  ./gradlew bootRun --debug-jvm
  ```
* **条件付きブレークポイント:** 特定条件下でのみ停止するようブレークポイントを設定
* **ウォッチ式:** 変数や式の値を監視
* **実行制御:** ステップイン、ステップオーバー、ステップアウト機能を活用

### その他の調査ツール

* **Spring Boot Actuator:** 現在非導入ですが、導入することでヘルスチェック、メトリクスなどの監視が可能になります
* **プロファイラ:** VisualVM、JProfilerなどのJavaプロファイラを使用してパフォーマンス問題やメモリリークを調査

## 質問テンプレート

問題が解決しない場合は、チームに質問するときに以下のテンプレートを使用してください。具体的な情報を提供することで、より適切な回答が得られます：

```markdown
## 問題
[問題の簡潔な説明]

## エラーメッセージ/ログ
```
[エラーメッセージや関連するログを貼り付け]
```

## 再現手順
1. [手順1]
2. [手順2]
3. [手順3]

## 試したこと
* [調査1]
* [調査2]

## 期待する結果
[本来期待される動作]

## 環境情報
* OS: [OS名/バージョン]
* JDK: [JDKバージョン]
* Docker: [Dockerバージョン]
* IDE: [IDE名/バージョン]
```