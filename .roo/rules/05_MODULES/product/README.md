# Product Management モジュール

商品、カテゴリ、在庫、プロモーションを管理するモジュールです。

## 責務
- 商品カタログ管理（商品、カテゴリ）
- 在庫管理
- プロモーション/割引管理

## 主要コンポーネント

### ドメインモデル
- `Product`: 商品情報を表す集約ルート
- `Category`: 商品カテゴリを表すモデル
- `Inventory`: 在庫管理のためのモデル
- `Promotion`: 商品プロモーションを表すモデル

### 値オブジェクト
- `ProductId`: 商品ID（UUIDベースの不変値オブジェクト）
- `CategoryId`: カテゴリID（UUIDベースの不変値オブジェクト）
- `InventoryId`: 在庫ID（UUIDベースの不変値オブジェクト）
- `PromotionId`: プロモーションID（UUIDベースの不変値オブジェクト）

### ファクトリー
- `ProductFactory`: 商品の生成を担当（IDの生成含む）
- `CategoryFactory`: カテゴリの生成を担当（IDの生成含む）
- `InventoryFactory`: 在庫の生成を担当（IDの生成含む）
- `PromotionFactory`: プロモーションの生成を担当（IDの生成含む）

### リポジトリ
- `Products`: 商品の検索・保存
- `Categories`: カテゴリの検索・保存
- `Inventories`: 在庫の検索・保存・更新
- `Promotions`: プロモーションの検索・保存

### ワークフロー
- `CreateProductWorkflow`: 商品作成フロー
- `AdjustInventoryWorkflow`: 在庫調整フロー
- `CreatePromotionWorkflow`: プロモーション作成フロー

## 依存関係
- `share`: 共通コンポーネント利用（ドメイン例外、値オブジェクトなど）

## データモデル
商品関連データは以下のコレクションに格納：
- `products`: 商品情報
- `categories`: カテゴリ情報
- `inventories`: 在庫情報
- `promotions`: プロモーション情報