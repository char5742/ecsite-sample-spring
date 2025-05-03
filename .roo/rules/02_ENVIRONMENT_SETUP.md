# 開発環境構築

## 必須ツール

### 1. JDK 23
- **GraalVM JDK 23**推奨（他のOpenJDK 23ディストリビューションも可能）
- 環境変数: `JAVA_HOME`と`PATH`にbin追加
- 確認: `java --version`

### 2. Docker
- MongoDB実行用（任意）
- 代替: MongoDB Community Serverのローカルインストール
- 確認: `docker --version`

### 3. Git
- バージョン管理用
- 初期設定
  ```bash
  git config --global user.name "Your Name"
  git config --global user.email "you@example.com"
  ```

## セットアップ手順

### プロジェクト取得
```bash
git clone <リポジトリURL> ecsite-sample-spring
cd ecsite-sample-spring
```

### MongoDBセットアップ
```bash
# Docker使用（推奨）
docker run --name local-mongo -p 27017:27017 -v ecsite-mongo-data:/data/db -d mongo:latest

# 接続確認（任意）
docker exec -it local-mongo mongosh
```

### コードフォーマッター
- Spotlessによる自動フォーマット
- Lefthookによるコミット時自動適用
- 手動実行: `./gradlew spotlessApply`

### 動作確認
```bash
./gradlew build
```

成功したら環境構築完了！