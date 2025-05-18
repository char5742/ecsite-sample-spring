# ECサイト API テスト (Runn)

このディレクトリには、Runn を使用した API テストが含まれています。

## Runn とは

Runn は、API のシナリオテストを YAML 形式で記述・実行できるテストツールです。

## インストール

### macOS
```bash
brew tap k1LoW/tap
brew install runn
```

### Linux/Windows (Go install)
```bash
go install github.com/k1LoW/runn/cmd/runn@latest
```

### Docker
```bash
docker pull ghcr.io/k1low/runn:latest
```

## ディレクトリ構造

```
test/
├── README.md
├── api-tests/          # API テストケース
│   ├── auth/           # 認証関連のテスト
│   ├── userprofile/    # ユーザープロファイル関連のテスト
│   └── shopping/       # ショッピング関連のテスト
├── fixtures/           # テストデータ
└── scripts/            # テスト実行スクリプト
```

## テスト実行

### 単一のテスト実行
```bash
runn run test/api-tests/auth/login.yml
```

### 全てのテスト実行
```bash
runn run test/api-tests/**/*.yml
```

### タグ指定実行
```bash
runn run test/api-tests/**/*.yml --tag auth
```

### 冗長モード
```bash
runn run test/api-tests/**/*.yml --verbose
```

## テストの書き方

テストは YAML ファイルで記述します。基本的な構造は以下の通りです：

```yaml
# テストの説明
desc: ユーザーログインテスト

# ベースURL
host: http://localhost:8080

# タグ
labels:
  - auth
  - login

# テストステップ
steps:
  - desc: ログインリクエスト
    req:
      method: POST
      path: /api/authentication/login
      headers:
        Content-Type: application/json
      body:
        email: test@example.com
        password: password123
    test:
      # ステータスコードの検証
      status: 200
      # レスポンスボディの検証
      body:
        token: /^\S+$/  # 正規表現でJWTトークンの形式を検証
```

## 環境変数

環境別の設定は環境変数で管理します：

```bash
export API_BASE_URL=http://localhost:8080
export TEST_USER_EMAIL=test@example.com
export TEST_USER_PASSWORD=password123
```

テスト内で環境変数を使用：

```yaml
host: {{ env.API_BASE_URL }}

steps:
  - desc: ログイン
    req:
      method: POST
      path: /api/authentication/login
      body:
        email: {{ env.TEST_USER_EMAIL }}
        password: {{ env.TEST_USER_PASSWORD }}
```

## テスト前準備

1. MongoDB が起動していることを確認
2. アプリケーションが起動していることを確認
3. 必要に応じてテストデータを投入

## トラブルシューティング

- `connection refused` エラー: アプリケーションが起動しているか確認
- 認証エラー: JWT トークンの有効期限を確認
- タイムアウト: `--timeout` オプションでタイムアウト時間を調整

## 参考リンク

- [Runn 公式ドキュメント](https://github.com/k1LoW/runn)
- [Runn チュートリアル](https://runn.run/docs/)