# ECサイト API テスト実行ガイド

このガイドでは、Runn を使用した API テストの実行方法について説明します。

## 前提条件

1. **Runn がインストールされていること**
   - インストール方法は `test/README.md` を参照

2. **アプリケーションが起動していること**
   ```bash
   ./gradlew bootRun
   ```

3. **MongoDB が起動していること**
   ```bash
   docker start local-mongo
   ```

## テスト実行方法

### 1. 全テスト実行

すべての API テストを実行する場合：

```bash
cd test
./scripts/run-tests.sh
```

### 2. 特定のテストファイル実行

特定のテストファイルのみを実行する場合：

```bash
cd test
./scripts/run-specific-test.sh api-tests/auth/login.yml
```

### 3. タグ指定実行

特定のタグが付いたテストのみを実行する場合：

```bash
cd test
./scripts/run-tests-by-tag.sh auth
```

利用可能なタグ：
- `auth`: 認証関連すべて
- `signup`: サインアップのみ
- `login`: ログインのみ
- `userprofile`: ユーザープロファイル関連
- `address`: 住所管理
- `shopping`: ショッピング関連すべて
- `cart`: カート操作
- `add-item`: カートへの商品追加

### 4. 手動実行

Runn コマンドを直接使用する場合：

```bash
cd test
runn run api-tests/auth/login.yml
```

冗長モードで実行：

```bash
runn run api-tests/auth/login.yml --verbose
```

## 環境変数

以下の環境変数でテスト動作をカスタマイズできます：

| 環境変数 | 説明 | デフォルト値 |
|---------|------|-------------|
| `API_BASE_URL` | APIのベースURL | `http://localhost:8080` |

例：
```bash
export API_BASE_URL=http://localhost:3000
./scripts/run-tests.sh
```

## テスト結果の見方

### 成功時の出力例

```
📝 実行中: login
✅ 成功: login

📝 実行中: signup
✅ 成功: signup

========================================
テスト結果サマリー
========================================
合計テスト: 2
成功: 2
失敗: 0

🎉 全てのテストが成功しました！
```

### 失敗時の出力例

```
📝 実行中: login
❌ 失敗: login

========================================
テスト結果サマリー
========================================
合計テスト: 2
成功: 1
失敗: 1

失敗したテスト:
  - login

⚠️  1 個のテストが失敗しました
```

## トラブルシューティング

### よくある問題と解決方法

1. **`connection refused` エラー**
   - アプリケーションが起動しているか確認
   - ポート番号が正しいか確認（デフォルト: 8080）

2. **認証エラー (401 Unauthorized)**
   - JWTトークンの有効期限を確認
   - アカウントが正しく作成されているか確認

3. **テストが見つからない**
   - ファイルパスが正しいか確認
   - テストファイルが `.yml` 拡張子であることを確認

4. **タイムアウトエラー**
   - Runn のタイムアウト設定を調整：
     ```bash
     runn run test.yml --timeout 30s
     ```

## テストの追加方法

新しいテストを追加する際は、以下の手順に従ってください：

1. 適切なディレクトリにテストファイルを作成
   - 認証関連: `api-tests/auth/`
   - ユーザープロファイル関連: `api-tests/userprofile/`
   - ショッピング関連: `api-tests/shopping/`

2. テストファイルの基本構造：
   ```yaml
   desc: テストの説明
   host: {{ env.API_BASE_URL | default("http://localhost:8080") }}
   labels:
     - タグ1
     - タグ2
   steps:
     - desc: ステップの説明
       req:
         method: POST
         path: /api/endpoint
         headers:
           Content-Type: application/json
         body:
           key: value
       test:
         status: 200
         body:
           expectedKey: expectedValue
   ```

3. テストを実行して動作確認

## CI/CD 統合

GitHub Actions などの CI/CD パイプラインに統合する場合：

```yaml
- name: APIテスト実行
  run: |
    cd test
    ./scripts/run-tests.sh
```

## 参考資料

- [Runn 公式ドキュメント](https://github.com/k1LoW/runn)
- プロジェクトの API 仕様: `docs/openapi/entry.yml`
- アーキテクチャ概要: `docs/architect/overview.md`