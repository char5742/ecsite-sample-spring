# Sample モジュール

新規モジュール作成の参考となるサンプル実装です。

## 概要

このモジュールは、DDDに基づくオニオンアーキテクチャの標準的な実装パターンを示します。
新しいモジュールを作成する際のテンプレートとして使用してください。

## 責務

- サンプルエンティティの管理
- 作成・取得・更新・削除の基本操作
- ワークフローパターンの実装例

## 主要コンポーネント

### ドメインモデル
- `Sample`: サンプルエンティティ（集約ルート）
- `SampleId`: エンティティ識別子の値オブジェクト
- `SampleStatus`: 状態を表す列挙型
- `SampleFactory`: エンティティ生成のファクトリー

### リポジトリ
- `Samples`: リポジトリインターフェース
- `MongoSamples`: MongoDB実装

### ワークフロー
- `CreateSampleWorkflow`: サンプル作成のワークフロー
  - `ValidateInputStep`: 入力検証
  - `CreateSampleStep`: エンティティ作成
  - `SaveSampleStep`: 永続化

### API
- `SampleHandlers`: APIハンドラーインターフェース
- `CreateSampleHandler`: 作成APIエンドポイント

## アーキテクチャ構成

```
sample/
├── domain/               # ドメイン層
│   ├── models/          # エンティティ、値オブジェクト
│   ├── repositories/    # リポジトリインターフェース
│   └── services/        # ドメインサービス（ファクトリー）
├── application/         # アプリケーション層
│   ├── usecase/        # ユースケース
│   └── workflow/       # ワークフロー
├── infrastructure/      # インフラストラクチャ層
│   ├── api/           # APIハンドラー実装
│   ├── repository/    # リポジトリ実装
│   ├── stepimpl/      # ワークフローステップ実装
│   └── workflowimpl/  # ワークフロー実装
└── api/                # APIインターフェース
```

## 実装パターン

### 1. 値オブジェクト

```java
public record SampleId(UUID value) {
  public SampleId {
    if (value == null) {
      throw new IllegalArgumentException("SampleId value cannot be null");
    }
  }
}
```

### 2. ファクトリーパターン

```java
@Component
@RequiredArgsConstructor
public class SampleFactory {
  private final IdGenerator idGenerator;
  private final TimeProvider timeProvider;
  
  public Sample create(String name, @Nullable String description) {
    // エンティティ生成ロジック
  }
}
```

### 3. ワークフローパターン

```java
public abstract class CreateSampleWorkflow implements CreateSampleUsecase {
  // ステップを組み合わせてユースケースを実装
}
```

### 4. リアクティブパターン

```java
public Mono<Sample> execute(String name, @Nullable String description) {
  return validateInputStep
      .execute(name, description)
      .then(createSampleStep.execute(name, description))
      .flatMap(saveSampleStep::execute);
}
```

## テスト戦略

- **単体テスト**: ドメインモデル、ワークフロー、ステップ
- **統合テスト**: リポジトリ実装、APIハンドラー
- **モック使用**: 外部依存性の分離

## 参考にすべき点

1. **レイヤー分離**: 各層の責務を明確に分離
2. **依存性の方向**: 内側への依存のみ（依存性逆転の原則）
3. **インターフェース定義**: 抽象に依存する設計
4. **ファクトリーパターン**: オブジェクト生成の責務を集約
5. **ワークフローパターン**: 複雑な処理をステップに分解
6. **リアクティブプログラミング**: 非同期処理の実装
7. **テスト容易性**: モックを使用した単体テスト
8. **エラーハンドリング**: 適切な例外処理

## 新規モジュール作成手順

1. このモジュールをコピーして新しいモジュール名に変更
2. パッケージ名を適切な名前に変更
3. クラス名を適切な名前に変更
4. ビジネスロジックに応じて実装を調整
5. 必要に応じて追加のコンポーネントを実装
6. テストを実装して動作を確認