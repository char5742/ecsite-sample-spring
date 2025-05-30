# Share モジュール

他モジュールで共通利用される基盤コンポーネントを提供します。

## 主要コンポーネント

- **`Email`**: メールアドレスの値オブジェクト
- **`AuditInfo`**: 監査情報（作成日時、更新日時）を保持するrecord
- **`IdGenerator`**: UUID生成インターフェース
- **`TimeProvider`**: 現在時刻取得のためのインターフェース
- **`DomainException`**: 基底例外クラス

## 設計方針

- 他モジュールから依存される基盤モジュール
- 逆方向の依存関係は持たない（他モジュールへの依存なし）
- 外部依存（時刻取得など）はインターフェースで抽象化し、テスト容易性を向上