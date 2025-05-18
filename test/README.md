# ECサイト API テスト

DockerとRunnによるAPIテストです。複雑なセットアップは不要です。

## 必要なもの
- Docker
- Make
- 起動中のECサイトアプリケーション（別ターミナルで `./gradlew bootRun`）

## クイックスタート

```bash
# テストを実行
cd test
make test
```

## 主なコマンド

```bash
# すべてのテストを実行
make test

# 特定のテストを実行
make test-one FILE=api-tests/auth/login.yml

# Dockerイメージをビルド（初回または更新時）
make build

# クリーンアップ
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

## トラブルシューティング

- アプリケーションは起動しているか？（`./gradlew bootRun`）
- MongoDBは起動しているか？
- ポート8080が使用可能か？

## 詳細
- Runnの公式イメージを使用
- ローカルにGoやRunnのインストール不要