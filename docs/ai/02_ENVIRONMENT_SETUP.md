# 2. 開発環境構築ガイド

このドキュメントでは、`ecsite-v2` プロジェクトの開発に必要な環境をセットアップする手順を説明します。

## 前提知識

開発を始める前に、以下の技術に関する基本的な知識があるとスムーズです。

*   Java プログラミング言語
*   Spring Framework / Spring Boot の基本概念
*   Git によるバージョン管理
*   Docker の基本操作

## 必須ツール

以下のツールをインストールし、設定してください。手順は各ツールの公式サイトを参照してください。

### 1. JDK (Java Development Kit)

*   **必須バージョン:** JDK 23 (`build.gradle` で指定されています)
*   **インストール:**
    *   お使いのOSに対応する JDK 23 をインストールしてください。OpenJDK ディストリビューション (例: [Adoptium Temurin](https://adoptium.net/), [Amazon Corretto](https://aws.amazon.com/corretto/) など) の利用が一般的です。
*   **設定:**
    *   環境変数 `JAVA_HOME` にJDK 23のインストールディレクトリを設定します。
    *   システムの `PATH` 環境変数に `$JAVA_HOME/bin` (Linux/macOS) または `%JAVA_HOME%\bin` (Windows) を追加します。
*   **確認:** ターミナル（コマンドプロンプト）で以下のコマンドを実行し、バージョンが表示されることを確認します。
    ```bash
    java --version
    javac --version
    ```

### 2. Docker (推奨)

*   **目的:** ローカルでのMongoDBデータベースの実行に推奨されるツールです (必須ではありません)。Dockerを使用しない場合は、別途MongoDBをローカル環境にインストールする必要があります。
*   **インストール (Dockerを使用する場合):**
    *   [Docker Desktop 公式サイト](https://www.docker.com/products/docker-desktop/) から、お使いのOSに対応するインストーラをダウンロードして実行します。
*   **確認 (Dockerを使用する場合):** Docker Desktopを起動し、正常に動作することを確認します。ターミナルで以下のコマンドがエラーなく実行できることを確認します。
    ```bash
    docker --version
    docker compose version
    ```

### 3. Git

*   **目的:** ソースコードのバージョン管理に使用します。
*   **インストール:**
    *   [Git 公式サイト](https://git-scm.com/downloads) からダウンロードするか、OSのパッケージマネージャ（Homebrew, apt, etc.）でインストールします。
*   **初期設定:** ターミナルで以下のコマンドを実行し、ユーザー名とメールアドレスを設定します。
    ```bash
    git config --global user.name "Your Name"
    git config --global user.email "your.email@example.com"
    ```
*   **確認:** ターミナルで以下のコマンドを実行し、バージョンが表示されることを確認します。
    ```bash
    git --version
    ```

### 4. Gradle

*   **注意:** このプロジェクトでは Gradle Wrapper (`./gradlew` または `gradlew.bat`) を使用するため、**別途Gradleをインストールする必要はありません。** プロジェクト内のWrapperが適切なバージョンのGradleを自動的にダウンロードして利用します。

## プロジェクトの取得

1.  **リポジトリのクローン:** ターミナルを開き、プロジェクトを配置したいディレクトリに移動して、以下のコマンドを実行します。（リポジトリURLは適宜変更してください）
    ```bash
    git clone <リポジトリのURL> ecsite-v2
    cd ecsite-v2
    ```

## プロジェクトのインポート

*   お使いのテキストエディタやIDE（VSCode, IntelliJ IDEA Community/Ultimate など）で、クローンした `ecsite-v2` ディレクトリを標準的なGradleプロジェクトとして開いてください。
*   IDEによっては、初回オープン時にGradleの依存関係解決やインデックス作成に時間がかかる場合があります。
*   プロジェクトの文字コード設定が `UTF-8` になっていることを確認してください。

## ローカルデータベース (MongoDB) のセットアップ

ローカル開発環境でMongoDBデータベースを実行する必要があります。

1.  **MongoDBの起動:**
    *   **Dockerを使用する場合 (推奨):**
        プロジェクトのルートディレクトリ (`ecsite-v2`) でターミナルを開き、以下のコマンドを実行してMongoDBコンテナをバックグラウンドで起動します (ポート、ボリュームは適宜調整)。
        ```bash
        docker run --name local-mongo -p 27017:27017 -v mongo-data:/data/db -d mongo:latest
        ```
        *   初回実行時はMongoDBのDockerイメージがダウンロードされるため、時間がかかることがあります。
        *   `docker-compose.yml` はこのプロジェクトに含まれていません。
    *   **Dockerを使用しない場合:**
        *   お使いのOSにMongoDB Community Serverを直接インストールし、サービスを起動してください。インストール手順は[MongoDB公式サイト](https://www.mongodb.com/try/download/community)を参照してください。

2.  **接続確認 (任意):**
    *   **コマンドラインクライアント (`mongosh`):**
        ```bash
        # mongosh がインストールされている場合
        mongosh mongodb://localhost:27017

        # Dockerコンテナ内で実行する場合
        docker exec -it <コンテナ名 or ID> mongosh
        ```
    *   **GUIツール (MongoDB Compassなど):**
        *   MongoDB CompassなどのGUIツールを使用すると、データベースの状態を視覚的に確認できて便利です。
        *   接続文字列 `mongodb://localhost:27017` を使用して接続します。

3.  **初期データ:**
    *   もし初期データ投入用のスクリプトや手順があれば、それに従ってください。（例: `./gradlew loadInitialData` など）

## コードフォーマッター (Spotless)

*   このプロジェクトでは Spotless を使用してコードスタイルを統一しています。
*   コードをコミットする前に、フォーマットを適用することをお勧めします。
*   **フォーマットチェック:**
    ```bash
    ./gradlew spotlessCheck
    ```
*   **フォーマット適用:**
    ```bash
    ./gradlew spotlessApply
    ```

## 最終動作確認

*   プロジェクトのルートディレクトリで以下のコマンドを実行し、ビルドが成功することを確認します。
    ```bash
    ./gradlew build
    ```
    *   `BUILD SUCCESSFUL` と表示されれば、基本的な環境構築は完了です。
    *   テストが失敗する場合は、[10_TROUBLESHOOTING.md](./10_TROUBLESHOOTING.md) を参照してください。

これで開発を開始する準備が整いました。次は [03_BUILD_RUN_DEBUG.md](./03_BUILD_RUN_DEBUG.md) に進み、プロジェクトのビルド、実行、デバッグ方法を学びましょう。
