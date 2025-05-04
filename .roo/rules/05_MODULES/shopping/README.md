# Shopping モジュール

ショッピングカート、注文、決済に関する機能を提供するモジュールです。

## 責務
- カート管理（作成、商品追加・削除、量の変更）
- 注文処理（カートからの注文作成、状態管理）
- 決済処理（支払い開始、承認、完了）

## 主要コンポーネント

### ドメインモデル
- **`Cart`**: ショッピングカートを表す集約ルート
  - `CartItem`: カート内の商品アイテム
- **`Order`**: 注文を表す集約ルート
  - `OrderItem`: 注文内の商品アイテム
  - `OrderStatus`: 注文状態（CREATED, PAID, SHIPPED, DELIVERED, COMPLETED, CANCELLED）
- **`Payment`**: 支払いを表す集約ルート
  - `PaymentStatus`: 支払い状態（PENDING, AUTHORIZED, CAPTURED, REFUNDED, PARTIALLY_REFUNDED, FAILED, CANCELLED）
  - `PaymentError`: 支払いエラー情報（エラーコード、エラーメッセージ）

### ドメインサービス
- **`CartFactory`**: カートオブジェクトの生成を担当
- **`OrderFactory`**: 注文オブジェクトの生成を担当
- **`PaymentFactory`**: 支払いオブジェクトの生成を担当

### ID値オブジェクト
- `CartId`: カートID
- `OrderId`: 注文ID
- `PaymentId`: 支払いID

### ドメインイベント
- **`CartEvent`**: カートに関するイベント（ItemAdded, ItemRemoved, ItemQuantityUpdated, Cleared）
- **`OrderEvent`**: 注文に関するイベント（OrderPlaced, OrderPaid, OrderShipped, OrderDelivered, OrderCompleted, OrderCancelled）
- **`PaymentEvent`**: 支払いに関するイベント（PaymentInitiated, PaymentAuthorized, PaymentCaptured, PaymentRefunded, PaymentFailed, PaymentCancelled）

### リポジトリ
- **`Carts`**: カートの永続化と検索
- **`Orders`**: 注文の永続化と検索
- **`Payments`**: 支払いの永続化と検索
  - `MongoPayments`: Paymentsリポジトリのインフラ実装
  - `PaymentDocument`: MongoDB用のドキュメントクラス

### ワークフロー
#### カート関連
- **`GetOrCreateCartWorkflow`**: アカウントIDに紐づくカートを取得または新規作成
- **`AddItemToCartWorkflow`**: カートに商品を追加
- **`RemoveItemFromCartWorkflow`**: カートから商品を削除
- **`UpdateItemQuantityWorkflow`**: カート内の商品数量を更新
- **`ClearCartWorkflow`**: カート内の全商品を削除

#### 注文関連
- **`CreateOrderFromCartWorkflow`**: カートから注文を作成
- **`GetOrderWorkflow`**: 注文の取得
- **`CancelOrderWorkflow`**: 注文のキャンセル
- **`MarkOrderAsShippedWorkflow`**: 注文を出荷済みに更新
- **`MarkOrderAsDeliveredWorkflow`**: 注文を配送済みに更新

#### 支払い関連
- **`InitiatePaymentWorkflow`**: 支払い処理の開始
- **`AuthorizePaymentWorkflow`**: 支払いの承認
- **`CapturePaymentWorkflow`**: 支払いの確定
- **`RefundPaymentWorkflow`**: 支払いの返金

## 主要フロー

### 商品購入フロー
1. ユーザーがカートに商品を追加（`AddItemToCartWorkflow`）
2. カートから注文を作成（`CreateOrderFromCartWorkflow`）
3. 支払い処理を開始（`InitiatePaymentWorkflow`）
4. 支払いを承認（`AuthorizePaymentWorkflow`）
5. 支払いを確定（`CapturePaymentWorkflow`）
6. 注文状態を更新（自動または管理者操作）

### 設計方針
- ドメインモデルは不変（イミュータブル）設計
- 状態変更は新しいインスタンスを返す
- ドメインイベントによる変更履歴の追跡
- ファクトリーパターンによるオブジェクト生成の一元管理
- リアクティブプログラミングによる非同期処理（Mono/Flux）
- ワークフローは単一目的の処理に特化
- 監査情報やエラー情報は専用クラスに分離
- 不要なnullチェックは省略し、パッケージレベルでnonnull設定を活用