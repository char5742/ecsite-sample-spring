# Docker 環境構築ガイド

このドキュメントでは、Docker Compose を使用した開発環境の構築方法について説明します。

## 概要

Docker Compose を使用することで、以下のコンポーネントを含む開発環境を簡単に構築できます：

- **MongoDB**: データベース
- **Spring Boot アプリケーション**: ECサイトバックエンド
- **Runn テストコンテナ**: API テスト実行

## 前提条件

- Docker Engine 20.10 以上
- Docker Compose 2.0 以上
- Git

## セットアップ手順

### 1. リポジトリのクローン

```bash
git clone <repository-url>
cd ecsite-sample-spring
```

### 2. 環境変数の設定

```bash
cp .env.example .env
```

必要に応じて `.env` ファイルを編集し、環境変数を設定します。

### 3. Docker Compose の起動

```bash
# バックグラウンドで起動
docker compose up -d

# ログを表示しながら起動
docker compose up
```

### 4. アプリケーションの確認

アプリケーションが正常に起動したか確認：

```bash
# コンテナの状態確認
docker compose ps

# アプリケーションのログ確認
docker compose logs app

# ヘルスチェック
curl http://localhost:8080/actuator/health
```

### 5. API テストの実行

API テストを実行：

```bash
# テストコンテナを実行
docker compose run --rm api-tests

# 特定のテストのみ実行
docker compose run --rm api-tests /bin/sh -c "cd /app/test && ./scripts/run-specific-test.sh api-tests/auth/login.yml"
```

## Docker Compose コマンド

### 基本コマンド

```bash
# 起動
docker compose up -d

# 停止
docker compose down

# 再起動
docker compose restart

# ログ表示
docker compose logs -f

# 特定サービスのログ
docker compose logs -f app
```

### データベース操作

```bash
# MongoDB シェルへ接続
docker compose exec mongodb mongosh -u admin -p password --authenticationDatabase admin

# データベースのバックアップ
docker compose exec mongodb mongodump -u admin -p password --authenticationDatabase admin --out /tmp/backup

# データベースのリストア
docker compose exec mongodb mongorestore -u admin -p password --authenticationDatabase admin /tmp/backup
```

### 開発中の操作

```bash
# アプリケーションの再ビルド
docker compose build app

# 全サービスの再ビルド
docker compose build

# キャッシュなしでビルド
docker compose build --no-cache

# 特定サービスのみ再起動
docker compose restart app
```

## トラブルシューティング

### ポート競合

既存のプロセスがポートを使用している場合：

1. `.env` ファイルでポートを変更
2. または既存プロセスを停止

```bash
# ポート使用状況確認
lsof -i :8080
lsof -i :27017
```

### データベース接続エラー

1. MongoDB コンテナが起動しているか確認
2. 接続文字列が正しいか確認
3. ネットワーク設定を確認

```bash
# ネットワーク確認
docker network ls
docker network inspect ecsite-sample-spring_ecsite-network
```

### ビルドエラー

1. Docker のディスク容量を確認
2. 不要なイメージやコンテナを削除

```bash
# クリーンアップ
docker system prune -a
```

## 環境変数一覧

| 変数名 | 説明 | デフォルト値 |
|-------|------|------------|
| `JWT_SECRET_KEY` | JWT 署名キー | `your-secret-key-for-jwt` |
| `JWT_EXPIRATION_TIME` | JWT 有効期限（ミリ秒） | `3600000` |
| `MONGO_ROOT_USERNAME` | MongoDB 管理者ユーザー名 | `admin` |
| `MONGO_ROOT_PASSWORD` | MongoDB 管理者パスワード | `password` |
| `MONGO_DATABASE` | データベース名 | `ecsite` |

## データボリューム

MongoDB のデータは名前付きボリュームに保存されます：

```bash
# ボリューム確認
docker volume ls

# ボリューム詳細
docker volume inspect ecsite-sample-spring_mongodb_data

# ボリューム削除（注意：データが削除されます）
docker volume rm ecsite-sample-spring_mongodb_data
```

## セキュリティに関する注意

本番環境では必ず以下を実施してください：

1. 強力なパスワードを使用
2. JWT シークレットキーを安全に管理
3. 環境変数を適切に設定
4. ネットワークセキュリティを強化
5. 不要なポートは公開しない

## 関連リンク

- [Docker 公式ドキュメント](https://docs.docker.com/)
- [Docker Compose 公式ドキュメント](https://docs.docker.com/compose/)
- [MongoDB Docker イメージ](https://hub.docker.com/_/mongo)
- [Eclipse Temurin Docker イメージ](https://hub.docker.com/_/eclipse-temurin)