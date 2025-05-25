# Sample モジュール

新規モジュール作成の参考となるサンプルモジュールです。このモジュールは、DDDに基づくオニオンアーキテクチャの完全な実装例として提供されています。

## 責務
- DDD実装パターンのリファレンス実装
- オニオンアーキテクチャの実装例
- ワークフロー/ステップパターンの実装例

## 主要コンポーネント

### ドメインモデル（domain/models）
- `Sample`: サンプルエンティティ（集約ルート）
  - 識別子による同一性
  - 名前、説明、ステータス、監査情報を保持
  - `updateName()`, `updateDescription()`, `updateStatus()` メソッドで状態変更
- `SampleStatus`: サンプルの状態を表す列挙型（ACTIVE, INACTIVE）
- `SampleId`: サンプルの識別子を表す値オブジェクト（recordとして実装）

### ドメインイベント（domain/events）
- `SampleEvent`: サンプルエンティティに関するドメインイベントのsealedインターフェース
  - `SampleCreated`: サンプル作成イベント
  - `SampleNameUpdated`: 名前更新イベント
  - `SampleDescriptionUpdated`: 説明更新イベント
  - `SampleStatusChanged`: ステータス変更イベント
  - `SampleDeleted`: サンプル削除イベント

### ドメイン例外（domain/exceptions）
- `SampleNotFoundException`: サンプルが見つからない場合の例外（DomainExceptionを継承）

### リポジトリ（domain/repositories）
- `Samples`: サンプルエンティティのリポジトリインターフェース
  - `findById(SampleId)`: IDによる検索（Mono<Sample>を返す）
  - `findByName(String)`: 名前による検索（Flux<Sample>を返す）
  - `save(Sample)`: 保存（Mono<Sample>を返す）
  - `deleteById(SampleId)`: IDによる削除（Mono<Void>を返す）

### ドメインサービス（domain/services）
- `SampleFactory`: サンプルエンティティの生成を担当
  - `IdGenerator`と`TimeProvider`を注入
  - `create(name, description)`メソッドで新規エンティティ生成

### ワークフロー（application/workflow）
- `CreateSampleWorkflow`: サンプル作成処理のオーケストレーション
  - 3つのステップを順次実行
  - 各ステップの結果を次のステップに渡す

### ユースケース（application/usecase）
- `CreateSampleUsecase`: サンプル作成ユースケース
  - `CreateSampleWorkflow`を利用してサンプル作成処理を実行

### インフラストラクチャ実装（infrastructure）
- **APIハンドラー（api）**
  - `SampleHandlers`: ハンドラーインターフェース
  - `SampleHandlersImpl`: Spring WebFluxベースの実装
- **リポジトリ実装（repository）**
  - `MongoSamples`: MongoDBを使用したリポジトリ実装
  - `SampleDocument`: MongoDBドキュメントモデル
  - `SampleDocumentRepository`: Spring Data MongoDBリポジトリ
- **ステップ実装（stepimpl）**
  - `ValidateInputStepImpl`: 入力検証ステップの実装
  - `CreateSampleStepImpl`: サンプル作成ステップの実装
  - `SaveSampleStepImpl`: サンプル保存ステップの実装
- **ワークフロー実装（workflowimpl）**
  - `CreateSampleWorkflowImpl`: ワークフローの具体的な実装

## パッケージ構造
```
sample/
├── SampleId.java            # 値オブジェクト
├── api/                     # APIエンドポイント
│   ├── SampleHandlers.java
│   └── SampleHandlersImpl.java
├── application/             # アプリケーション層
│   ├── usecase/
│   │   └── CreateSampleUsecase.java
│   └── workflow/
│       └── CreateSampleWorkflow.java
├── domain/                  # ドメイン層
│   ├── events/
│   │   └── SampleEvent.java
│   ├── exceptions/
│   │   └── SampleNotFoundException.java
│   ├── models/
│   │   ├── Sample.java
│   │   └── SampleStatus.java
│   ├── repositories/
│   │   └── Samples.java
│   └── services/
│       └── SampleFactory.java
├── infrastructure/          # インフラストラクチャ層
│   ├── api/                # （実装詳細）
│   ├── repository/
│   │   ├── MongoSamples.java
│   │   ├── SampleDocument.java
│   │   └── SampleDocumentRepository.java
│   ├── stepimpl/
│   │   ├── CreateSampleStepImpl.java
│   │   ├── SaveSampleStepImpl.java
│   │   └── ValidateInputStepImpl.java
│   └── workflowimpl/
│       └── CreateSampleWorkflowImpl.java
└── package-info.java       # モジュール定義（shareに依存）
```

## 設計方針

- **エンティティの実装例**: IDによる同一性とミュータブルな状態を持つ
- **値オブジェクトの実装例**: recordによる不変実装
- **ドメインイベントの実装例**: sealed interfaceによる型安全な実装
- **リポジトリパターンの実装例**: ドメイン層でのインターフェース定義、インフラ層での実装
- **ワークフロー/ステップパターンの実装例**: 複数ステップの処理をオーケストレーション
- **例外処理の実装例**: ドメイン例外の階層的な実装
- **リアクティブプログラミング**: 全てのメソッドがMono/Fluxを返す非同期実装
- **Null安全性**: JSpecifyアノテーションを使用