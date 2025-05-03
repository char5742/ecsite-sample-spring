# 7. テストガイド

このドキュメントでは、`ecsite-v2` プロジェクトにおけるテスト戦略、各種テストの実装方法、およびテストに関するガイドラインについて説明します。

## テスト戦略

本プロジェクトでは、一般的に「テストピラミッド」と呼ばれる考え方に基づき、以下の階層でテストを構成します。

1. **単体テスト (Unit Tests):** 最も数が多く、高速に実行できるテスト。個々のクラスやメソッドのロジックを検証します。外部依存はモック化します。
2. **結合テスト (Integration Tests):** 複数のコンポーネントやモジュール、または外部システム (DBなど) との連携を検証するテスト。単体テストより数は少なく、実行に時間がかかります。
3. **E2Eテスト (End-to-End Tests):** (現在は実施していません) 実際のユーザー操作を模倣し、システム全体の動作を検証するテスト。最も実行コストが高いです。

## 単体テスト (Unit Tests)

**対象:**
- **ドメインモデル** (`Account`, `EmailAuthentication` など): 不変性、バリデーション、ビジネスロジックの検証
- **Workflow** (`LoginWorkflow` など): 依存するStepをモック化し、ステップ呼び出し順序やロジックフローを検証
- **Usecase** (`LoginUsecase` など): 依存するWorkflowやRepositoryをモック化し、アプリケーション層のロジックを検証
- **Stepインターフェース**: 振る舞いを定義通りに呼び出すかの検証
- **ユーティリティクラス**

**ツール:** JUnit 5, Mockito (モック化), AssertJ (アサーション)

**リアクティブコードのテスト:** `StepVerifier`を使用して`Mono`や`Flux`の発行するシグナルを検証

**実装例:**
- `src/test/java/com/example/ec_2024b_back/auth/domain/models/EmailAuthenticationTest.java`
- `src/test/java/com/example/ec_2024b_back/auth/domain/workflow/LoginWorkflowTest.java`
- `src/test/java/com/example/ec_2024b_back/auth/application/usecase/LoginUsecaseTest.java`

## 結合テスト (Integration Tests)

**対象:**
- **Repository実装** (`MongoAccounts`など): 実際のDB (Testcontainersで起動) との連携を検証
- **Step実装** (`FindAccountByEmailStepImpl`など): DBや他のインフラコンポーネントとの連携を検証
- **APIハンドラー/コントローラー層**: リクエスト受付からレスポンス返却までの一連の流れを検証

**ツール/アノテーション:**
- `@SpringBootTest`: Spring Boot アプリケーションコンテキスト全体をロードしてテスト実行
- `@DataMongoTest`: MongoDB との連携に特化したテストスライス
- `@IntegrationTest` (`com.example.ec_2024b_back.utils.IntegrationTest`): `@SpringBootTest`と`@ActiveProfiles("test")`の組み合わせ
- **Testcontainers**: Dockerコンテナで一時的なMongoDB環境を提供
- `WebTestClient`: リアクティブなAPIエンドポイントのテスト用クライアント

**テスト用プロファイル:** 現在`src/test/resources`に`application-test.properties`は存在しません。テスト固有の設定が必要な場合は、Testcontainersの設定やテストクラス内でのプロパティ設定で行います。

## テストタグ

テストには適切なタグを付けることで実行時の選別や実行環境の区別を容易にします。必ず適切なタグを付けてください。

### 用意されているタグ

- **@Fast**: 実行時間が短く外部依存がない単体テスト（CIパイプラインでは常に実行）
- **@Slow**: 実行時間が長いテストや外部リソースに依存するテスト
- **@IntegrationTest**: 複数コンポーネントの統合テスト
- **@DatabaseTest**: データベースとの結合テスト
- **@ApiTest**: APIエンドポイントのテスト

### タグの使用方法

```java
import com.example.ec_2024b_back.utils.Fast;
import org.junit.jupiter.api.Test;

@Fast
class FastUnitTest {
    @Test
    void someTest() {
        // テスト実装
    }
}
```

クラスレベルでタグを適用すると、そのクラス内のすべてのテストメソッドにタグが適用されます。メソッドレベルでタグを適用することも可能です。

### タグ付けのガイドライン

- **すべてのテストに少なくとも1つのタグを付ける**: テストはその特性に応じて適切にタグ付けする
- **複数のタグの併用**: テストの性質に応じて複数のタグを適用可能
- **新しいタグの追加**: 必要に応じて新しいタグを追加する場合はチームでの合意が必要

## テスト実行方法

```bash
# 全テスト実行
./gradlew test

# 特定タグのテスト実行
./gradlew test -PincludeTags="Fast"

# 特定タグのテスト除外
./gradlew test -PexcludeTags="Slow"

# 複数タグ指定 (OR条件)
./gradlew test -PincludeTags="Fast | IntegrationTest"

# カバレッジレポート生成
./gradlew test jacocoTestReport
```

## テスト実装ガイドライン

### 命名規則
- **テストクラス**: `<対象クラス>Test` (例: `LoginWorkflowTest`)
- **テストメソッド**: `should<期待される結果>_when<テスト条件>` (例: `shouldReturnTokenWhenCredentialsAreValid`)
  - 日本語での記述も可。その場合は一貫性をもたせて使用すること。

### テスト構造
- **Arrange-Act-Assert パターン** (または Given-When-Then) を使用
  - **Arrange**: テストの準備（データ作成、モック設定等）
  - **Act**: テスト対象メソッド実行
  - **Assert**: 結果検証
- テストメソッド内で準備、実行、検証の各ステップを明確に分離（コメントや空行で区切る）

### カバレッジ
- **測定ツール**: JaCoCo Gradleプラグイン 
  - レポートは`./gradlew test jacocoTestReport`実行後`build/reports/jacoco/test/html/index.html`で確認
- **CI連携**: GitHub Actions上でカバレッジレポート生成・コメント投稿に[Octocov](https://github.com/k1LoW/octocov)を使用
- **目標値**: プロジェクトポリシーに従うが、重要なドメインロジックや複雑な処理は高カバレッジを目指す

### モック利用例
```java
@ExtendWith(MockitoExtension.class)
class ExampleTest {
    @Mock
    private ExternalDependency dependency;
    
    @InjectMocks
    private ServiceUnderTest service;
    
    @Test
    void test() {
        // Arrange
        when(dependency.method()).thenReturn(expectedValue);
        
        // Act
        Result result = service.methodUnderTest();
        
        // Assert
        assertThat(result).isEqualTo(expectedValue);
    }
}
```

### リアクティブテスト例
```java
@Test
void reactiveTest() {
    // Arrange
    Mono<String> mono = service.getReactiveMono();
    
    // Act & Assert
    StepVerifier.create(mono)
        .expectNext("expected value")
        .verifyComplete();
}
```

### その他の推奨事項
- **テストデータ**: 各テストメソッド内で準備するか`@BeforeEach`を使用。テストデータ生成ユーティリティの作成も検討。
- **アサーション**: AssertJを活用し、流暢で読みやすい検証コードを記述。
- **テスト分離**: テスト間で依存関係を持たせない。各テストが独立して実行できることを確保。
- **境界値テスト**: エッジケースやバリデーション境界値のテストを積極的に作成。
- **実行速度考慮**: テストの実行速度に応じて`@Fast`または`@Slow`タグを適切に付与。