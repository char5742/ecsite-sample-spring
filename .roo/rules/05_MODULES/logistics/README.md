# Logistics モジュール

ECサイトの配送・物流管理機能を提供するモジュールです。注文情報と連携し、配送情報の管理、配送状態の追跡を担当します。

## 責務

- 配送情報の作成と管理
- 配送状態の遷移と管理
- 配送情報の追跡（追跡番号管理）
- 注文モジュールとの連携

## 主要コンポーネント

### ドメインモデル（domain/models）

- **`Shipment`**: 配送情報を表す集約ルート
  - 注文ID、配送先住所、配送方法、状態、追跡番号などを保持
  - `updateStatus()`: 配送状態を更新（状態遷移ルールに従う）
  - `markArrived()`: 配送到着を記録
  - `markDelivered()`: 配送完了を記録
  - `markReturned()`: 返送を記録
  - 静的ファクトリメソッド：`create()`, `reconstruct()`
- **`ShipmentStatus`**: 配送状態を表す列挙型
  - CREATED: 新規作成
  - PENDING: 出荷待機中
  - SHIPPED: 出荷済み・配送中
  - ARRIVED: 配送先に到着
  - DELIVERED: 配送完了
  - ON_HOLD: 保留中
  - RETURNED: 返送
  - `canTransitionTo()`: 状態遷移の検証メソッド

### ドメインイベント（domain/models）

- **`ShipmentEvent`**: 配送に関するドメインイベント（sealedインターフェース）
  - `ShipmentCreated`: 配送情報作成イベント
  - `ShipmentStatusUpdated`: 配送状態更新イベント
  - `ShipmentArrived`: 配送到着イベント
  - `ShipmentDelivered`: 配送完了イベント
  - `ShipmentReturned`: 配送返送イベント

### 値オブジェクト

- **`ShipmentId`**: 配送IDを表す値オブジェクト（recordとして実装）

### リポジトリ（domain/repositories）

- **`Shipments`**: 配送情報のリポジトリインターフェース
  - `findById(ShipmentId)`: IDによる検索（Mono<Shipment>を返す）
  - `findByOrderId(OrderId)`: 注文IDによる検索（Flux<Shipment>を返す）
  - `save(Shipment)`: 保存（Mono<Shipment>を返す）

### ドメインサービス（domain/services）

- **`ShipmentFactory`**: 配送情報を生成するファクトリークラス
  - `IdGenerator`と`TimeProvider`を注入
  - `create(orderId, shippingAddress, shippingMethod, estimatedDeliveryDate)`: 新規配送情報を生成

## 主要フロー

### 配送作成フロー
1. `ShipmentFactory`を使用して注文情報から配送情報を作成
2. 配送状態をCREATEDに設定
3. 必要に応じて配送予定日を設定
4. リポジトリに保存

### 配送状態更新フロー
1. 配送IDから配送情報を検索
2. `updateStatus()`メソッドで配送状態を更新（遷移ルールに基づいて検証）
3. 必要に応じて追跡番号や備考を追加
4. 更新された配送情報を保存

### 配送完了フロー
1. 配送IDから配送情報を検索
2. `markDelivered()`メソッドで配送完了状態に更新
3. 受取人名や配送完了日時を記録
4. 更新された配送情報を保存

## 配送状態の遷移ルール

- **CREATED** → PENDING, RETURNED
- **PENDING** → SHIPPED, RETURNED
- **SHIPPED** → ARRIVED, ON_HOLD, RETURNED
- **ARRIVED** → DELIVERED, ON_HOLD, RETURNED
- **ON_HOLD** → SHIPPED, ARRIVED, DELIVERED, RETURNED
- **DELIVERED, RETURNED** → （最終状態、遷移不可）

## 依存関係

- **Share モジュール**: 共通コンポーネント（DomainException, AuditInfo, IdGenerator, TimeProvider）を利用
- **Shopping モジュール**: OrderIdを参照（注文情報との連携）

## パッケージ構造
```
logistics/
├── ShipmentId.java         # 値オブジェクト
├── domain/                 # ドメイン層
│   ├── models/
│   │   ├── Shipment.java
│   │   ├── ShipmentEvent.java
│   │   └── ShipmentStatus.java
│   ├── repositories/
│   │   └── Shipments.java
│   └── services/
│       └── ShipmentFactory.java
├── application/            # アプリケーション層（未実装）
├── infrastructure/         # インフラストラクチャ層（未実装）
└── package-info.java      # モジュール定義（share, shoppingに依存）
```

## 設計方針

- **集約ルート**: Shipmentがイベントとライフサイクルを管理
- **イベントソーシング**: ドメインイベントを内部的に記録（将来の拡張用）
- **ファクトリーパターン**: ShipmentFactoryで生成ロジックを集約
- **状態パターン**: ShipmentStatusで状態遷移ルールを管理
- **リアクティブプログラミング**: 全てのリポジトリメソッドがMono/Fluxを返す
- **Null安全性**: JSpecifyアノテーションを使用（@NullMarked）