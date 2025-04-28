**ECシステム構成 概要書**

---

# 1. システムの箇所概要

現代的なイベント験感を検討したECサービスを構築するため，以下の要素を格定とした構成を採用する：

- データ駆動型 (Event-Driven Architecture)
- 書き込み/読み取り分離 (CQRS)
- イベントソーシングによる状態管理
- インフラをコード化 (IaC)
- Kubernetes環境に配置

これにより，スケーラビリティ，安全性，拡張性，運用性に優れたシステムを実現する。

---

# 2. 構成要素

## 2.1 データベース
- **主データベース**：MongoDBを採用し，スキーマ管理を簡素化
- **CDC-Outboxパターン**：主DBの変更をOutboxコレクションに記録し，緊密な一調性を保持

## 2.2 メッセージ基盤
- **Kafka**：イベントストリーミングによる非同期通信基盤

## 2.3 APIレイヤ
- **APIGateway (Kong)**：外部向けの入口を集中管理，JWT証明による認証，レート制御やIP制限など軽量フィルタリングを実装
- **WAF**：Kongにより証明，フィルタリング、メトリクス制御を行う
- **認証許可**：OAuth2.0 / OIDCに基づくID管理

## 2.4 バックエンド
- **BFF (Hasura)**：Kongで認証されたリクエストを受け、行レベルセキュリティ (Row-Level Security)を用いた認可管理を行う
- **Application Layer (BE)**：Command (DB書き込み)を管理する。商用ロジック、トランザクション管理
- **Domain Layer**：ビジネスロジック実装
- **Infrastructure Layer**：Kafkaプロデューサー，MongoDBアクセス

## 2.5 オブザービリティ
- **ログ管理**：Kong，Hasura，BEからのログをFluent Bit経由でLokiに集約し，Grafanaで可視化
- **メトリクス管理**：Kubernetesの各Pod/コンテナの資源利用統計をPrometheusで取得し，Grafanaで可視化
- **アラート構築**：Prometheusのルールを用いたアラート発火

## 2.6 インフラ構成
- **IaC (Infrastructure as Code)**：TerraformによるProxmox VE環境管理

---

# 3. トランザクション管理
- **Command** (FE→BE→DB)：一調性保持，ドメインロジックを通じた書き込み
- **Query** (FE→APIGateway(Kong)→BFF(Hasura)→DB)：Kongで認証を完了させ，HasuraがJWTに基づく行レベル認可を実装して読み取りを行う
- 変更イベントはCDC経由でKafkaに伝播

---

# 4. 脆弱性対応
- **GitHub Advanced Security (GHAS)**を利用し，コードの脆弱性検知，依存関係の脆弱性自動PRを発行
- **Trivy**とGitHub Actionsを連携し，Dockerイメージの脆弱性スキャンを実施

