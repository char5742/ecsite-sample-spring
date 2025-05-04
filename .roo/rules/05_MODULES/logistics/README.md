# Logistics モジュール

ECサイトの配送・物流管理機能を提供するモジュールです。注文情報と連携し、配送情報の管理、配送状態の追跡を担当します。

## 責務

- 配送情報の作成と管理
- 配送状態の遷移と管理
- 配送情報の追跡（追跡番号管理）
- 注文モジュールとの連携

## 主要コンポーネント

### ドメインモデル

- **`Shipment`**: 配送情報を表す集約ルート
- **`ShipmentStatus`**: 配送状態を表す列挙型（CREATED, PENDING, SHIPPED, ARRIVED, DELIVERED, ON_HOLD, RETURNED）
- **`ShipmentEvent`**: 配送に関するドメインイベント

### 値オブジェクト

- **`ShipmentId`**: 配送IDを表す値オブジェクト

### リポジトリ

- **`Shipments`**: 配送情報の検索・保存（`findById`, `findByOrderId`, `save`）

### ワークフロー

- **`CreateShipmentWorkflow`**: 注文から配送情報を作成
- **`UpdateShipmentStatusWorkflow`**: 配送状態を更新
- **`MarkShipmentDeliveredWorkflow`**: 配送完了を記録

## 主要フロー

### 配送作成フロー
1. 注文情報を元に配送情報を作成
2. 配送状態をCREATEDに設定
3. 必要に応じて配送予定日を設定

### 配送状態更新フロー
1. 配送IDから配送情報を検索
2. 配送状態を更新（遷移ルールに基づいて検証）
3. 必要に応じて追跡番号や備考を追加

### 配送完了フロー
1. 配送IDから配送情報を検索
2. 配送完了状態に更新
3. 受取人名や配送完了日時を記録
4. 関連する注文情報の状態も更新

## イベント

- **`ShipmentCreated`**: 配送情報が作成されたイベント
- **`ShipmentStatusUpdated`**: 配送状態が更新されたイベント
- **`ShipmentArrived`**: 配送が目的地に到着したイベント
- **`ShipmentDelivered`**: 配送が完了したイベント
- **`ShipmentReturned`**: 配送が返送されたイベント

## 依存関係

- **Share モジュール**: 共通コンポーネントを利用
- **Shopping モジュール**: 注文情報との連携