# コーディング規約

## 基本方針
- **可読性・保守性・一貫性・シンプルさ**を重視

## 命名規則
- **パッケージ**: 小文字スネークケース (`com.example.ec_2024b_back.auth.domain`)
- **クラス・インターフェース・列挙型**: アッパーキャメルケース (`Account`)
- **メソッド・変数**: ローワーキャメルケース (`findByEmail`)
- **定数**: アッパースネークケース (`DEFAULT_TIMEOUT`)
- **テスト**: `クラス名Test`, `shouldXxxWhenYyy`

## 自動フォーマット
- **Spotless**: コミット前に自動適用 (`./gradlew spotlessApply`)

## コメント・テキスト
- **JavaDoc**: 日本語で記述、目的と引数・戻り値を明記
- **エラーメッセージ**: 日本語で記述、必要な識別情報を含める
- **TODO**: `// TODO: 内容 担当者/Issue番号`

## Java実装ガイドライン

### 型・データ操作
- **var使用**: ローカル変数のみ、型が明白な場合
- **Stream API**: 積極的に活用、過度に複雑な場合はメソッド抽出
- **Optional**: 戻り値のみ、フィールド・引数には非推奨
- **Lombok**: ボイラープレート削減 (`@Getter`, `@RequiredArgsConstructor`等)
- **値オブジェクト**: IDなどの識別子は不変のrecordとして実装 (`record ProductId(UUID value)`)
- **ファクトリーパターン**: オブジェクト生成の責務は専用のファクトリークラスに委譲
- **時刻操作**: `LocalDateTime.now()`などの直接呼び出しは避け、`TimeProvider`のような抽象化インターフェースを使用
- **監査情報の分離**: 作成日時・更新日時などの監査情報は`AuditInfo`クラスに集約
- **エラー情報の分離**: エラーコード・エラーメッセージなどのエラー情報は専用のクラスに集約（例: `PaymentError`）
- **警告の抑制**: TooManyParametersなどの警告は必要に応じて`@SuppressWarnings`で抑制（ビルダーパターン導入前に検討）

### 設定・プロパティ
- **推奨**: `@ConfigurationProperties`による型安全なクラス
- **検証**: `@Validated`と`@NotNull`等で起動時検証

### 静的解析・Null安全性

#### NullAway
- パッケージレベルで`@NullMarked`適用
- Null可能性は`@Nullable`で明示
- 外部データ境界では`@NullUnmarked`可

#### パッケージ構造 (auth/userprofile)
```
module/
├── api/                  # APIエンドポイント
├── application/          # アプリケーション層
│   └── workflow/         # ワークフロー実装
├── domain/               # ドメイン層
│   ├── models/           # ドメインモデル
│   └── repositories/     # リポジトリIF
└── infrastructure/       # インフラ層
    └── repository/       # リポジトリ実装
```