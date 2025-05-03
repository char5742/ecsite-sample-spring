# データベース設計

MongoDB（ドキュメント指向NoSQL）を採用。リアクティブドライバーで非同期アクセスを実現。

## コレクション

- **`accounts`**: アカウント情報と認証情報
- **`user_profiles`**: ユーザープロファイルと住所情報
- **`products`**: 商品情報
- **`categories`**: カテゴリ情報
- **`inventories`**: 在庫情報
- **`promotions`**: プロモーション情報

## スキーマ詳細

### `accounts` コレクション

アカウントと認証情報を保存します。

```mermaid
erDiagram
    accounts {
        String _id PK "アカウントID"
        List~AuthenticationDocument~ authentications "認証情報リスト"
    }
    accounts ||--o{ authentications : contains
```

**推奨インデックス:** `authentications.email`

### `user_profiles` コレクション

ユーザープロファイルと住所情報を保存します。

```mermaid
erDiagram
    user_profiles {
        String _id PK "プロファイルID"
        String name "ユーザー名"
        String accountId FK "アカウントID"
        List~AddressDocument~ addresses "住所リスト"
    }
    user_profiles ||--o{ addresses : contains
```

**推奨インデックス:** `accountId`

### `products` コレクション

商品情報を保存します。

```mermaid
erDiagram
    products {
        String _id PK "商品ID"
        String name "商品名"
        String description "商品説明"
        Number price "価格"
        String categoryId FK "カテゴリID"
        List~String~ images "画像URL"
    }
```

**推奨インデックス:** `categoryId`, `name`

### `categories` コレクション

商品カテゴリ情報を保存します。

```mermaid
erDiagram
    categories {
        String _id PK "カテゴリID"
        String name "カテゴリ名"
        String description "カテゴリ説明"
        String parentId FK "親カテゴリID（任意）"
    }
```

**推奨インデックス:** `parentId`, `name`

### `inventories` コレクション

在庫情報を保存します。

```mermaid
erDiagram
    inventories {
        String _id PK "在庫ID"
        String productId FK "商品ID"
        Number quantity "数量"
        String status "ステータス（在庫あり/なし等）"
    }
```

**推奨インデックス:** `productId`

### `promotions` コレクション

プロモーション情報を保存します。

```mermaid
erDiagram
    promotions {
        String _id PK "プロモーションID"
        String name "プロモーション名"
        String description "説明"
        String type "割引タイプ（額/率/etc）"
        Number value "割引値"
        Date startDate "開始日"
        Date endDate "終了日"
        List~String~ productIds "対象商品ID"
        List~String~ categoryIds "対象カテゴリID"
    }
```

**推奨インデックス:** `startDate`, `endDate`

## 設計ノート

- **埋め込みパターン:** 関連データを単一ドキュメント内に埋め込み
- **マイグレーション:** 専用ツール未導入。スキーマ変更は手動対応
