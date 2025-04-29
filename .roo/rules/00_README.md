# ecsite-v2 オンボーディングガイド

ようこそ！ このドキュメント群は、`ecsite-v2` プロジェクトの開発に参加する新しいメンバーが、迅速かつスムーズに開発を開始できるようにするためのガイドです。

## 対象読者

*   このプロジェクトに新たに参加する開発者
*   プロジェクトの概要や開発プロセスを再確認したい既存メンバー

## このガイドの歩き方

以下の順序で読み進めることを推奨します。

1.  **[01_PROJECT_OVERVIEW.md](./01_PROJECT_OVERVIEW.md):** まずはこのドキュメントを読み、プロジェクトの全体像、目的、技術スタック、基本的な構造を理解してください。
2.  **[02_ENVIRONMENT_SETUP.md](./02_ENVIRONMENT_SETUP.md):** 開発に必要な環境を構築します。
3.  **[03_BUILD_RUN_DEBUG.md](./03_BUILD_RUN_DEBUG.md):** プロジェクトのビルド、ローカルでの実行、デバッグ方法を学びます。
4.  **[08_WORKFLOW.md](./08_WORKFLOW.md):** 開発を進める上での基本的なワークフロー（Git Flow, Pull Request, コードレビュー）を確認します。

上記を完了すれば、基本的な開発タスクを開始できるはずです。

より深く理解するため、または特定のタスクに取り組む際には、以下のドキュメントを参照してください。

---

### ドメイン層とインフラ層の責務分離について

本プロジェクトでは、クリーンアーキテクチャの原則に基づき、ドメイン層とインフラ層の明確な責務分離を実践しています。

#### 基本原則

1.  **技術的実装の隠蔽**: MongoDBのDocument (`AccountDocument` 等) はインフラ層でのみ扱い、ドメイン層やアプリケーション層では必ずドメインモデル (`Account` 等の集約) を利用します。
2.  **集約（Aggregate）の境界**: 各ドメインモデルは明確な集約境界を持ち、データの一貫性を保証します。例えば、`Account` 集約はアカウント関連のエンティティと値オブジェクトをカプセル化します。
3.  **リポジトリの抽象化**: リポジトリインターフェース (`Accounts` 等) はドメイン層に定義され、その実装 (`MongoAccounts` 等) はインフラ層にあります。これによりドメイン層はデータアクセス実装の詳細から保護されます。
4.  **ドメインモデルの独立性**: ドメインモデルは永続化技術やデータベースから独立しています。データベースの変更があっても、ドメインロジックへの影響を最小限に抑えられます。

#### 実装パターン

1.  **リポジトリパターン**: ドメイン層にはリポジトリのインターフェースのみを定義し、実装はインフラ層に配置します。

    ```java
    // ドメイン層のリポジトリインターフェース (例: auth モジュール)
    package com.example.ec_2024b_back.auth.domain.repositories;
    // ... imports ...
    public interface Accounts {
        Mono<Account> findByEmail(Email email);
        Mono<Account> save(Account account);
        // ... 他のメソッド
    }

    // インフラ層のリポジトリ実装 (例: auth モジュール)
    package com.example.ec_2024b_back.auth.infrastructure.repository;
    // ... imports ...
    @Repository
    @RequiredArgsConstructor
    public class MongoAccounts implements Accounts {
        private final ReactiveMongoRepository<AccountDocument, String> repository; // Spring Data Reactive Repository

        @Override
        public Mono<Account> findByEmail(Email email) {
            return repository.findAccountDocumentByAuthenticationsEmail(email.value())
                .map(AccountDocument::toDomain); // ドキュメントからドメインへ変換
        }

        @Override
        public Mono<Account> save(Account account) {
            return repository.save(AccountDocument.fromDomain(account)) // ドメインからドキュメントへ変換
                .map(AccountDocument::toDomain);
        }
        // ... 他の実装
    }
    ```

2.  **ドメインモデルとドキュメントモデルの変換**: インフラ層のドキュメントモデル (`AccountDocument`) やリポジトリ実装 (`MongoAccounts`) 内で、ドメインモデルとの相互変換を行います。

    ```java
    // インフラ層のドキュメントクラス (例: auth モジュール)
    package com.example.ec_2024b_back.auth.infrastructure.repository;
    // ... imports ...
    @Document(collection = "accounts")
    @Data
    @NullUnmarked // NullAway: このクラスではnull許容を前提とする
    public class AccountDocument {
        @Id private String id;
        private List<AuthenticationDocument> authentications;
        // ... 他のフィールド

        // ドキュメントからドメインモデルへの変換
        public Account toDomain() {
            var domainAuthentications = Optional.ofNullable(this.authentications)
                .orElseGet(List::of)
                .stream()
                .map(AuthenticationDocument::toDomain) // ネストしたドキュメントも変換
                .collect(ImmutableList.toImmutableList());
            return new Account(new AccountId(this.id), domainAuthentications);
        }

        // ドメインモデルからドキュメントへの変換 (static factory method)
        public static AccountDocument fromDomain(Account account) {
            var doc = new AccountDocument();
            doc.setId(account.id().value());
            doc.setAuthentications(
                account.authentications().stream()
                    .map(AuthenticationDocument::fromDomain) // ネストしたドメインも変換
                    .toList()
            );
            return doc;
        }
    }

    // ネストした認証情報のドキュメントモデル (抜粋)
    @Data
    @NullUnmarked
    static class AuthenticationDocument {
        private String type;
        private String email;
        private String password;

        public Authentication toDomain() {
            // 実際の型に応じて適切なドメインモデルを生成
            if ("email".equals(this.type)) {
                return new EmailAuthentication(new Email(this.email), this.password);
            }
            // 他の認証タイプがあれば追加
            throw new IllegalStateException("Unknown authentication type: " + this.type);
        }

        public static AuthenticationDocument fromDomain(Authentication auth) {
            var doc = new AuthenticationDocument();
            if (auth instanceof EmailAuthentication emailAuth) {
                doc.setType("email");
                doc.setEmail(emailAuth.email().value());
                doc.setPassword(emailAuth.password()); // 注意: パスワードの扱い
            }
            // 他の認証タイプがあれば追加
            return doc;
        }
    }
    ```

#### 重要な実装規約

1.  **リポジトリメソッドの公開範囲**: インフラ層の `ReactiveMongoRepository` のメソッド (例: `findAccountDocumentByAuthenticationsEmail`) は直接外部に公開せず、ドメイン層のリポジトリインターフェース (`Accounts` の `findByEmail` など) を通じて利用してください。
2.  **トランザクション境界**: トランザクション境界はアプリケーション層（Usecase）に設定し、リポジトリ層では単純なデータアクセス操作のみを提供します。（MongoDBのトランザクションサポートは限定的なため、設計に注意が必要です）
3.  **値オブジェクトの不変性**: 値オブジェクト (`Email`, `AccountId` など) は不変 (`Immutable`, Java Record を活用) にし、ビジネスルールをカプセル化します。

#### メリット

*   **テスト可能性の向上**: モックリポジトリを使用してドメインロジックを単体テストできます。
*   **技術的実装の変更容易性**: データベース技術を変更しても、ドメイン層は影響を受けません。
*   **ビジネスルールの明確な表現**: ドメインモデルはビジネスルールを純粋に表現します。
*   **並行開発の効率化**: インフラチームとドメインチームが並行して開発できます。

#### モジュール間の依存関係管理

本プロジェクトでは現在、主に以下のモジュールが定義されています。

*   **auth**: 認証・認可関連
*   **share**: 複数モジュールで共有されるコンポーネント

各モジュール間の依存関係は明確に管理されています。詳細については [`docs/ai/04_ARCHITECTURE.md`](./04_ARCHITECTURE.md) を参照してください。

---

*   **[04_ARCHITECTURE.md](./04_ARCHITECTURE.md):** システムアーキテクチャの詳細（Modulith, リアクティブ, API設計など）。
*   **[05_MODULES/](./05_MODULES/):** 各機能モジュール（`auth`, `share` など）の詳細な説明。
*   **[06_CODING_STANDARDS.md](./06_CODING_STANDARDS.md):** コーディング規約とベストプラクティス。
*   **[07_TESTING.md](./07_TESTING.md):** テスト戦略とテストコードの実装方法。
*   **[09_DATABASE.md](./09_DATABASE.md):** データベース（MongoDB）のスキーマ設計とマイグレーションについて。
*   **[11_API_USAGE.md](./11_API_USAGE.md):** APIの利用方法（Swagger UI, Postmanなど）。
*   **[10_TROUBLESHOOTING.md](./10_TROUBLESHOOTING.md):** 開発中によく遭遇する問題とその解決策。
*   **[12_GLOSSARY.md](./12_GLOSSARY.md):** プロジェクト固有の用語集。

## AIアシスタント (Cline) 利用時の注意

プロジェクトルートの `.roo/rules/instruction.md` ファイルには、AIアシスタント (Cline) に対する基本的な指示が記述されています。

*   **ドキュメントの参照:** Cline は作業開始前にこの `docs/ai/00_README.md` を確認するように指示されています。
*   **ドキュメントの更新:** 作業完了後、関連ドキュメントの更新を忘れないように指示されています。
*   **不明点の確認:** 不明な点があれば、作業担当者に質問するように指示されています。

Cline を利用する際は、これらの指示が前提となっていることを念頭に置いてください。必要に応じて `.roo/rules/instruction.md` を更新することも検討してください。

## 貢献

このオンボーディング資料は常に改善されるべきものです。不明瞭な点、不足している情報、誤りなどを見つけた場合は、積極的に修正や追記のプルリクエストを作成してください。

資料の更新・改善に関する詳細なガイドラインは、以下のドキュメントを参照してください。

*   **[99_CONTRIBUTING_TO_DOCS.md](./99_CONTRIBUTING_TO_DOCS.md):** オンボーディング資料への貢献ガイド
