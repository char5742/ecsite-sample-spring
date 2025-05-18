# Sample モジュール

新規モジュール作成の参考となるサンプルモジュールです。

## 責務
- DDD実装パターンのリファレンス実装
- オニオンアーキテクチャの実装例
- ワークフローパターンの実装例

## 主要コンポーネント

### ドメインモデル
- `Sample`: サンプルエンティティ（集約ルート）
- `SampleStatus`: サンプルの状態を表す列挙型（ACTIVE, INACTIVE）
- `SampleId`: サンプルの識別子を表す値オブジェクト

### ドメインイベント
- `SampleEvent`: サンプルエンティティに関するドメインイベントのsealedインターフェース
  - `SampleCreated`: サンプル作成イベント
  - `SampleNameUpdated`: 名前更新イベント
  - `SampleDescriptionUpdated`: 説明更新イベント
  - `SampleStatusChanged`: ステータス変更イベント
  - `SampleDeleted`: サンプル削除イベント

### ドメイン例外
- `SampleNotFoundException`: サンプルが見つからない場合の例外

### リポジトリ
- `Samples`: サンプルエンティティのリポジトリインターフェース
  - `findById`: IDによる検索
  - `findByName`: 名前による検索
  - `save`: 保存
  - `deleteById`: IDによる削除

### ドメインサービス
- `SampleFactory`: サンプルエンティティの生成を担当

### ワークフロー
- `CreateSampleWorkflow`: サンプル作成処理
  - `ValidateInputStep`: 入力検証ステップ
  - `CreateSampleStep`: サンプル作成ステップ
  - `SaveSampleStep`: サンプル保存ステップ

### ユースケース
- `CreateSampleUsecase`: サンプル作成ユースケース

## 設計方針

- **エンティティの実装例**: IDによる同一性とミュータブルな状態を持つ
- **値オブジェクトの実装例**: 不変で値による同一性を持つ
- **ドメインイベントの実装例**: sealed interfaceによる型安全な実装
- **リポジトリパターンの実装例**: ドメイン層でのインターフェース定義
- **ワークフローパターンの実装例**: 複数ステップの処理をオーケストレーション
- **例外処理の実装例**: ドメイン例外の階層的な実装
- **リアクティブプログラミング**: Mono/Fluxを使用した非同期処理