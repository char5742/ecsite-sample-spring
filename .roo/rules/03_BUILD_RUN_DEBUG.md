# ビルド・実行・デバッグガイド

## 主要Gradleコマンド

```bash
# ビルド - JAR作成、テスト実行
./gradlew build

# 開発サーバー起動 (http://localhost:8080)
./gradlew bootRun

# テスト実行のみ
./gradlew test

# フォーマットチェック
./gradlew spotlessCheck

# 自動フォーマット適用
./gradlew spotlessApply

# ビルド成果物クリーンアップ
./gradlew clean
```

## 実行オプション

### プロファイル指定

```bash
# 開発プロファイル
./gradlew bootRun -Dspring.profiles.active=dev

# JAR実行時
java -Dspring.profiles.active=dev -jar build/libs/ecsite-sample-spring-0.0.1-SNAPSHOT.jar
```

### 環境変数設定

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

### デバッグモード起動

```bash
# 簡易デバッグ
./gradlew bootRun --debug-jvm

# 詳細設定（ポート5005）
./gradlew bootRun -Dorg.gradle.jvmargs="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

### リアクティブデバッグ

パイプラインの可視化にはログオペレーターを活用：
```java
// 特定箇所にシグナルログを挿入
.log()  // onNext, onError, onCompleteなどをコンソール出力
```

## ログ設定

ログレベル変更（`application.properties`または起動引数）：
```
logging.level.root=DEBUG  # すべてのロガー
logging.level.com.example.ec_2024b_back=DEBUG  # プロジェクト固有
```