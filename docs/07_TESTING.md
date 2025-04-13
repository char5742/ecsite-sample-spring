# テスト戦略とベストプラクティス

このドキュメントでは、`ecsite-v2` プロジェクトにおけるテスト戦略と実装方法について説明します。

## 目次

- [テストの種類](#テストの種類)
- [テストタグ](#テストタグ)
- [テスト実行方法](#テスト実行方法)
- [テストの書き方のベストプラクティス](#テストの書き方のベストプラクティス)

## テストの種類

本プロジェクトでは以下の種類のテストを実装しています：

1. **単体テスト**：個々のクラスやメソッドの機能をテスト
2. **統合テスト**：複数のコンポーネントが連携して動作することをテスト
3. **リポジトリテスト**：データアクセス層の機能をテスト
4. **APIテスト**：エンドポイントの動作をテスト

## テストタグ

テストにはタグを付けることで、実行時の選別や実行環境の区別を容易にしています。必ず適切なタグを付けてください。

### 用意されているタグ

本プロジェクトでは以下のタグ（アノテーション）を用意しています：

- **@Fast**: 実行時間が短く、外部依存がない単体テスト。CIパイプラインでは常に実行されます。
- **@Slow**: 実行時間が長いテストや、外部リソースに依存するテスト。
- **@IntegrationTest**: 複数のコンポーネントの統合テスト。
- **@DatabaseTest**: データベースとの結合テスト。
- **@ApiTest**: APIエンドポイントのテスト。

### タグの使用方法

タグは以下のように適用します：

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

- **すべてのテストに少なくとも1つのタグを付ける**：テストはその特性に応じて、少なくとも1つのタグを付けてください。
- **複数のタグの併用**：テストの性質に応じて複数のタグを適用することができます。
- **新しいタグの追加**：必要に応じて新しいタグを追加できますが、チームでの合意が必要です。

## テスト実行方法

### 全テストの実行

```bash
./gradlew test
```

### タグによるテストの選別実行

特定のタグを持つテストのみを実行：

```bash
./gradlew test -PincludeTags="Fast"
```

特定のタグを持つテストを除外：

```bash
./gradlew test -PexcludeTags="Slow"
```

複数のタグを指定（OR条件）：

```bash
./gradlew test -PincludeTags="Fast | IntegrationTest"
```

## テストの書き方のベストプラクティス

### 命名規則

- テストクラス名は対象クラス名に `Test` を付加する（例: `LoginWorkflow` → `LoginWorkflowTest`）
- テストメソッド名は `[テスト対象メソッド]_should[期待される結果]_when[テスト条件]` の形式を推奨

### テスト構造

- **Arrange-Act-Assert** パターンを使用
  - **Arrange**: テストの準備（モックの設定など）
  - **Act**: テスト対象メソッドの実行
  - **Assert**: 結果の検証

### モックの利用

- 外部依存はモックに置き換える
- Mockitoを使用したモックの例：

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

### リアクティブテスト

Reactor を使用したリアクティブプログラミングのテストには `StepVerifier` を使用します：

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
