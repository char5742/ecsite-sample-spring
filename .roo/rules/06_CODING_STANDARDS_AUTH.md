# Auth モジュール コーディングスタイルガイド

このドキュメントは、`auth` モジュールに特化したコーディングスタイルと規約をまとめたものです。汎用的な規約については `../06_CODING_STANDARDS.md` も参照してください。

## 1. パッケージ構造
```
auth/
├── api/                  # APIエンドポイント (Handlerクラス)
├── application/          # アプリケーション層
│   └── usecase/          # ユースケース実装 (例: LoginUsecase)
├── domain/               # ドメイン層
│   ├── models/           # ドメインモデル (例: Account, Authentication)
│   ├── repositories/     # リポジトリインターフェース (例: Accounts)
│   ├── step/             # ビジネスプロセスステップインターフェース (例: FindAccountByEmailStep)
│   └── workflow/         # ビジネスプロセスフロー (例: LoginWorkflow)
├── infrastructure/       # インフラ層
│   ├── repository/       # リポジトリ実装 (例: MongoAccounts, AccountDocument)
│   ├── security/         # セキュリティ関連実装 (例: JsonWebTokenProvider)
│   └── stepimpl/         # ステップインターフェース実装 (例: FindAccountByEmailStepImpl)
└── package-info.java     # モジュール定義 (@Modulithic)
```

## 2. レイヤーアーキテクチャとDDDパターン
`auth` モジュールはクリーンアーキテクチャとドメイン駆動設計の原則に従います。

- **集約 (Aggregate):** `Account` が集約ルートです。
- **エンティティ (Entity):** 現在の `auth` モジュールでは明確なエンティティは `Account` のみです (`Authentication` は集約の一部)。
- **値オブジェクト (Value Object):** `AccountId`, `JsonWebToken`, `EmailAuthentication` (不変性を持ち、属性で識別される)。`Email` は `share` モジュールで定義。
- **リポジトリ (Repository):** ドメイン層にインターフェース (`Accounts`)、インフラ層に実装 (`MongoAccounts`) を配置。
- **ユースケース (Usecase):** アプリケーション層に配置 (`LoginUsecase`, `SignupUsecase`)。ドメイン層のワークフローを呼び出し、トランザクション境界などを管理。
- **ワークフロー (Workflow):** ドメイン層に配置 (`LoginWorkflow`, `SignupWorkflow`)。ビジネスプロセスフローを定義し、ステップを協調させる。
- **ステップ (Step):** ドメイン層にインターフェース (`FindAccountByEmailStep` など)、インフラ層に実装 (`FindAccountByEmailStepImpl` など) を配置。ワークフロー内の個別の処理単位。

## 3. コーディング規約 (Auth モジュール特有)
汎用規約に加えて、以下の点を意識してください。

### 命名規則 (例)
- リポジトリインターフェース: `Accounts` (複数形)
- リポジトリ実装: `MongoAccounts`
- ステップインターフェース: `XxxStep` (例: `GenerateJWTStep`)
- ステップ実装: `XxxStepImpl` (例: `GenerateJWTStepImpl`)
- ユースケース: `XxxUsecase` (例: `LoginUsecase`)
- ワークフロー: `XxxWorkflow` (例: `LoginWorkflow`)
- テストクラス: `TargetClassNameTest` (例: `LoginWorkflowTest`)

### クラス構造 (ステップの例)
```java
// ドメイン層: ステップインターフェース
package com.example.ec_2024b_back.auth.domain.step;
// ... imports ...
public interface GenerateJWTStep {
    JsonWebToken generate(Account account);
}

// インフラ層: ステップ実装
package com.example.ec_2024b_back.auth.infrastructure.stepimpl;
// ... imports ...
@Service // または @Component
@RequiredArgsConstructor
public class GenerateJWTStepImpl implements GenerateJWTStep {
    private final JsonWebTokenProvider jsonWebTokenProvider; // 依存性の注入

    @Override
    public JsonWebToken generate(Account account) {
        // JWT生成ロジック
        String tokenValue = jsonWebTokenProvider.generateToken(account.id().value());
        return new JsonWebToken(tokenValue);
    }
}
```

## 4. Null安全性
- `auth` パッケージ全体に `@NullMarked` が適用されています。
- 原則として全ての型を non-null として扱い、null を許容する場合は `@Nullable` を明示的に使用します。
- `Optional` の使用は、リポジトリからの結果など、値が存在しないことが明確な場合に限定します。
- 詳細は `../06_CODING_STANDARDS.md` の NullAway セクションを参照してください。

## 5. 依存関係管理
- Spring Modulith によりモジュール境界を定義 (`package-info.java`)。
- ドメイン層 (`auth.domain.*`) は他のレイヤー (application, infrastructure) に依存しません。
- アプリケーション層はドメイン層とインフラ層のインターフェースに依存します。
- インフラ層はドメイン層のインターフェースを実装し、必要に応じて他のインフラコンポーネントに依存します。

## 6. セキュリティ実装
- JWT の生成・検証ロジックは `JsonWebTokenProvider` に集約します。
- JWT の設定 (シークレットキー、有効期限) は `JWTProperties` で管理し、`application.properties` から注入します。
- パスワード検証は `VerifyWithPasswordStep` インターフェースで抽象化し、実装 (`VerifyWithPasswordStepImpl`) で Spring Security の `PasswordEncoder` を利用します。

## 7. テスト戦略
### テスト構成
```
test/
└── java/
    └── com/example/ec_2024b_back/
        └── auth/
            ├── application/
            │   └── usecase/  # ユースケーステスト (例: LoginUsecaseTest)
            ├── domain/
            │   ├── models/   # ドメインモデルテスト (例: EmailAuthenticationTest)
            │   └── workflow/ # ワークフローテスト (例: LoginWorkflowTest)
            └── infrastructure/
                ├── repository/ # リポジトリ統合テスト (未実装)
                └── stepimpl/   # ステップ実装テスト (必要に応じて)
```

### テストパターン例
- **モックテスト (Unit Test):**
    - `LoginUsecaseTest`: 依存する Workflow や Step を Mockito でモック化し、Usecase のロジック単体を検証。
    - `LoginWorkflowTest`: 依存する Step をモック化し、Workflow 内のステップ呼び出し順序や連携ロジックを検証。
    - ドメインモデルのテスト (`EmailAuthenticationTest`, `JsonWebTokenTest`): 不変性やバリデーションロジックを検証。
- **統合テスト (Integration Test):**
    - `MongoAccounts` のテスト (未実装): `@DataMongoTest` と Testcontainers を使用し、実際の MongoDB との連携 (保存、検索) を検証。
    - API エンドポイントのテスト (未実装): `@SpringBootTest` や `@WebFluxTest` と `WebTestClient` を使用し、リクエストからレスポンスまでの一連の流れを検証。

## 8. 例外処理
- `auth` モジュール固有のビジネスルール違反が発生した場合、`DomainException` (share) を継承したカスタム例外 (例: `AuthenticationFailedException`, `AccountNotFoundException` - 必要に応じて作成) をスローします。
- 例外コードや多言語対応は現状未実装です。例外メッセージは日本語で、原因特定に役立つ情報を含めます。

## 9. ロギング
- Lombok の `@Slf4j` を使用してロガーを取得します。
- ログイン試行、成功、失敗などの重要なイベントや、デバッグに有用な情報は適切なログレベル (INFO, DEBUG) で出力します。
- AUDIT レベルのログは現状使用していません。
- エラー発生時は、スタックトレースを含むエラーログを出力します。

## 10. コメント規約
- `../06_CODING_STANDARDS.md` のコメント規約に従います。
- 特にドメイン層のクラスやメソッドには、そのビジネス上の意味や責務を明確にする JavaDoc を記述します。
