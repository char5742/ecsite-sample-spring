# 3. ビルド・実行・デバッグガイド

このドキュメントでは、プロジェクトのビルド、ローカル環境での実行、およびデバッグ方法について説明します。コマンドはすべてプロジェクトのルートディレクトリ (`ecsite-v2`) で実行することを想定しています。

## Gradleタスク詳解

このプロジェクトではビルドツールとしてGradleを使用しています。主要なGradleタスクは以下の通りです。

*   `./gradlew build`
    *   **説明:** プロジェクト全体のコンパイル、テスト実行、JARファイルの作成を行います。
    *   **成果物:** `build/libs/` ディレクトリに実行可能なJARファイル (`ecsite-v2-*.jar`) が生成されます。テストレポートなども `build/reports/` 以下に生成されます。
    *   **実行タイミング:** コード変更後、最終的な動作確認や配布物作成時に実行します。

*   `./gradlew bootRun`
    *   **説明:** Spring Bootアプリケーションを開発モードで起動します。組み込みのWebサーバー (Netty) が起動し、リクエストを受け付けられる状態になります。
    *   **特徴:** コード変更時の自動リロード（設定による）などが有効になる場合があります。開発中の動作確認に便利です。
    *   **停止:** `Ctrl+C` で停止します。

*   `./gradlew test`
    *   **説明:** プロジェクト内のすべての単体テストおよび結合テストを実行します。
    *   **レポート:** `build/reports/tests/test/index.html` でテスト結果の詳細を確認できます。

*   `./gradlew spotlessCheck`
    *   **説明:** ソースコードが Spotless で定義されたコーディング規約に準拠しているかチェックします。
    *   **実行タイミング:** コミット前やCIで実行し、規約違反がないか確認します。

*   `./gradlew spotlessApply`
    *   **説明:** Spotless の規約に従ってソースコードを自動フォーマットします。
    *   **実行タイミング:** コード作成後、コミット前に実行してスタイルを統一します。

*   `./gradlew generateOpenApiCode`
    *   **説明:** `docs/openapi/entry.yml` のOpenAPI定義に基づいて、APIインターフェース (`*Api.java`)、Delegateインターフェース (`*Delegate.java`)、およびモデルクラスを `api-model` モジュールの `build/generated/` 以下に自動生成します。
    *   **実行タイミング:** OpenAPI定義 (`entry.yml`) を変更した場合に実行します。通常、`build` タスクの一部として自動実行されるように設定されていることが多いです。

*   `./gradlew clean`
    *   **説明:** `build` ディレクトリを削除し、ビルド成果物をクリーンアップします。
    *   **実行タイミング:** ビルドに関する問題が発生した場合や、完全にクリーンな状態からビルドし直したい場合に実行します。

*   **その他カスタムタスク:**
    *   プロジェクト固有のカスタムタスクがあれば、ここに追加します。（例: `loadInitialData`, `generateDocs` など）

## アプリケーションの実行方法

### 1. 開発モードでの実行 (`bootRun`)

最も一般的な開発中の実行方法です。

```bash
./gradlew bootRun
```

アプリケーションが起動し、デフォルトでは `http://localhost:8080` でアクセス可能になります。

### 2. JARファイルからの実行

`./gradlew build` で生成されたJARファイルを実行します。

```bash
java -jar build/libs/ecsite-v2-*.jar
```
*(注意: `*` の部分は実際のバージョン番号に置き換えてください)*

### プロファイル指定

Spring Bootのプロファイル（例: `dev`, `prod`, `test`）を切り替えて実行する場合は、`-Dspring.profiles.active` オプションを使用します。

```bash
# bootRun でプロファイル指定
./gradlew bootRun -Dspring.profiles.active=dev

# JAR実行でプロファイル指定
java -Dspring.profiles.active=dev -jar build/libs/ecsite-v2-*.jar
```
設定ファイル (`application-{profile}.properties` または `.yml`) でプロファイル固有の設定を管理します。

### 環境変数設定

アプリケーションが環境変数を参照する場合、実行時に設定できます。

```bash
# Linux/macOS
export MY_VARIABLE=value
./gradlew bootRun

# Windows (Command Prompt)
set MY_VARIABLE=value
./gradlew bootRun

# Windows (PowerShell)
$env:MY_VARIABLE="value"
./gradlew bootRun
```

## デバッグ

### 1. デバッグモードでの起動

アプリケーションをデバッグモードで起動するには、JVMのデバッグオプションを指定します。

```bash
./gradlew bootRun --debug-jvm
```
または、直接JVM引数を指定することもできます（ポート番号はデフォルトで5005）。

```bash
./gradlew bootRun -Dorg.gradle.jvmargs="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```
*   `suspend=n`: アプリケーションはデバッガの接続を待たずに起動します。デバッガ接続前に処理を進めたい場合は `suspend=y` にします。
*   `address=*:5005`: ポート5005でデバッガからの接続を待ち受けます。

### 2. デバッガのアタッチ

アプリケーションがデバッグモードで起動したら、お使いのIDEやデバッグツールを起動したJVMプロセスにアタッチします。

*   **ホスト:** `localhost`
*   **ポート:** `5005` (上記で指定したポート番号)

アタッチ後、ソースコードにブレークポイントを設定し、ステップ実行、変数値の確認などを行うことができます。

### 3. リアクティブデバッグ

Spring WebFlux (Project Reactor) を使用しているため、従来のデバッグ手法だけでは追跡が難しい場合があります。以下のツールや手法が役立ちます。

*   **Reactor Debug Agent:**
    *   より詳細なスタックトレースを提供し、非同期処理の追跡を容易にします。
    *   通常、`reactor-tools` 依存関係を追加し、特定のJVM引数を設定することで有効になります。（設定方法はプロジェクトの依存関係を確認してください）
*   **`log()` オペレータ:**
    *   Reactorのパイプラインの途中に `.log()` を挿入することで、その時点でのシグナル（`onNext`, `onError`, `onComplete` など）をコンソールに出力できます。デバッグしたい箇所を特定するのに役立ちます。
    ```java
    Flux.range(1, 5)
        .map(i -> i * 2)
        .log() // ここでシグナルをログ出力
        .filter(i -> i > 5)
        .subscribe();
    ```
*   **BlockHound:**
    *   リアクティブパイプライン内で意図せずブロッキング呼び出しを行っている箇所を検出するツールです。
    *   通常、テスト実行時に有効化され、ブロッキング呼び出しがあるとテストが失敗します。（導入されているかはテスト設定を確認してください）

## ログ

アプリケーションの動作状況やエラー情報はログに出力されます。

*   **ログ設定ファイル:**
    *   通常、`src/main/resources/logback-spring.xml` や `application.properties`/`yml` 内の `logging.*` プロパティでログの設定（出力先、フォーマット、レベルなど）が行われます。
*   **ログ出力先:**
    *   デフォルトではコンソールに出力されます。設定によってはファイル (`logs/app.log` など) にも出力される場合があります。
*   **ログレベル:**
    *   一般的なログレベルは `ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE` です（詳細度順）。
    *   開発中は `DEBUG` や `TRACE` レベルに設定すると、より詳細な情報が得られます。ログレベルは通常、設定ファイルや実行時引数で変更可能です。
    ```bash
    # application.properties での例
    # logging.level.com.example.ec_2024b_back=DEBUG

    # 実行時引数での例 (bootRun)
    # ./gradlew bootRun -Dlogging.level.com.example.ec_2024b_back=DEBUG
    ```
*   **ログの見方:**
    *   タイムスタンプ、ログレベル、スレッド名、ロガー名（通常はクラス名）、メッセージを確認します。
    *   エラー発生時はスタックトレースが出力されるため、問題箇所を特定する手がかりになります。

これで、プロジェクトのビルド、実行、デバッグの基本的な方法を理解できました。次は [04_ARCHITECTURE.md](./04_ARCHITECTURE.md) でアーキテクチャの詳細を確認するか、[08_WORKFLOW.md](./08_WORKFLOW.md) で開発ワークフローを学びましょう。
