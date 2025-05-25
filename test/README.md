# ECサイト API テスト

DockerとRunnによるAPIテストです。アプリケーション、MongoDB、テストランナーがすべてDocker Composeで一括管理されています。

## 必要なもの
- Docker
- Docker Compose
- Make（オプション）

## クイックスタート

```bash
# テストを実行（アプリケーション・MongoDBの起動からテスト実行まで自動）
cd test
docker-compose up --build api-tests

# または Makeを使用
make test
```

## 主なコマンド

### Docker Composeを直接使用する場合

```bash
# すべてのテストを実行（ビルドから実行まで）
docker-compose up --build api-tests

# 特定のテストを実行
docker-compose run --rm one-test /tests/api-tests/auth/login.yml

# バックグラウンドで環境を起動
docker-compose up -d app

# ログの確認
docker-compose logs -f

# 環境の停止とクリーンアップ
docker-compose down -v
```

### Makeを使用する場合

```bash
# すべてのテストを実行
make test

# 特定のテストを実行
make test-one FILE=api-tests/auth/login.yml

# 環境の起動のみ
make up

# 環境の停止
make down

# クリーンアップ（ボリューム含む）
make clean
```

## ディレクトリ構造
```
test/
├── api-tests/           # テストファイル（.yml）
│   ├── auth/           # 認証関連
│   ├── shopping/       # ショッピング関連
│   └── userprofile/    # ユーザープロファイル関連
├── docker-compose.yml   # Docker設定
├── Dockerfile          # テスト実行環境
├── Makefile           # コマンド定義
└── README.md          # このファイル
```

## ポート使用状況

テスト環境は既存の開発環境と競合しないように、以下のポートを使用します：
- MongoDB: 27018（ホスト側）→ 27017（コンテナ側）
- アプリケーション: 8081（ホスト側）→ 8080（コンテナ側）

## トラブルシューティング

### ポート競合エラー
既存のサービスとポートが競合する場合は、`docker-compose.yml`のポート設定を変更してください。

### ビルドエラー
```bash
# キャッシュをクリアして再ビルド
docker-compose build --no-cache
```

### テスト失敗
```bash
# アプリケーションのログを確認
docker-compose logs app

# MongoDBのログを確認
docker-compose logs mongodb
```

## 詳細
- Runnの公式イメージを使用
- ローカルにGoやRunnのインストール不要
- MongoDB、アプリケーション、テストランナーがすべて一つのDocker Composeで管理
- ヘルスチェックにより、依存サービスの準備完了を待ってからテストを実行