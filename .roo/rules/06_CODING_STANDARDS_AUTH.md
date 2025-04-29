# 認証モジュール コーディングスタイルガイド

## 1. パッケージ構造
```
auth/
├── api/                  # APIエンドポイント
├── application/          # アプリケーション層
│   └── usecase/          # ユースケース実装
├── domain/               # ドメイン層
│   ├── models/           # ドメインモデル
│   ├── repositories/     # リポジトリインターフェース
│   ├── step/             # 認証ステップインターフェース
│   └── workflow/         # 認証フローロジック
├── infrastructure/       # インフラ層
│   ├── repository/       # リポジトリ実装
│   ├── security/         # セキュリティ関連実装
│   └── stepimpl/         # ステップインターフェース実装
└── package-info.java     # モジュール定義
```

## 2. レイヤーアーキテクチャ
### ドメイン駆動設計(DDD)パターン
- **エンティティ**: `Account`, `JsonWebToken` など不変性を持つドメインモデル
- **値オブジェクト**: `EmailAuthentication` などの比較可能なオブジェクト
- **リポジトリ**: インターフェース(`AccountRepository`)と実装(`MongoAccountRepository`)の分離
- **ユースケース**: `LoginUsecase` がアプリケーション層でフローを制御
- **ワークフロー**: `LoginWorkflow` がドメイン層でビジネスロジックを保持

## 3. コーディング規約
### 命名規則
- インターフェース: `FindAccountByEmailStep`
- 実装クラス: `FindAccountByEmailStepImpl`
- ユースケース: `LoginUsecase`
- ワークフロー: `LoginWorkflow`
- テストクラス: `LoginWorkflowTest`

### クラス構造
```java
// インターフェース例
public interface GenerateJWTStep {
    JsonWebToken generate(Account account);
}

// 実装例
@Service
@RequiredArgsConstructor
public class GenerateJWTStepImpl implements GenerateJWTStep {
    // 実装コード
}
```

## 4. Null安全性
- 全クラスに `@NullMarked` アノテーションを付与
- JSPECIFYを使用したnull安全性保証
- Optionalの使用は最小限に抑え、明示的なnullチェックを推奨

## 5. 依存関係管理
- Spring Modulithを使用したモジュール化
- ドメイン層はインフラ層に依存しない
- インターフェース経由でのみ実装に依存

## 6. セキュリティ実装
- JWT生成/検証は `JsonWebTokenProvider` で集中管理
- シークレット管理は `JWTProperties` で設定
- パスワード検証は `VerifyWithPasswordStep` で抽象化

## 7. テスト戦略
### テスト構成
```
test/
└── java/
    └── com/example/ec_2024b_back/
        └── auth/
            ├── application/
            │   └── usecase/  # ユースケーステスト
            ├── domain/
            │   ├── models/   # モデルテスト
            │   └── workflow/ # ワークフローテスト
            └── infrastructure/
                └── repository/ # リポジトリ統合テスト
```

### テストパターン
- モックテスト: `LoginUsecaseTest` (サービス層の振る舞い検証)
- 統合テスト: `MongoAccountRepositoryTest` (DB連携検証)
- フローテスト: `LoginWorkflowTest` (全体フロー検証)

## 8. 例外処理
- ドメイン例外は `DomainException` を継承
- 例外コードは列挙型で管理
- エラーメッセージは多言語対応を前提としたコード管理

## 9. ロギング
- ロガーは Lombok の `@Slf4j` を使用
- 重要イベントは AUDIT レベルでログ出力
- エラー時はスタックトレースを出力

## 10. コメント規約
- Javadocは最低限の説明に留める
- 複雑なロジックはインラインコメントで説明
- TODOコメントは期限付きで記載
