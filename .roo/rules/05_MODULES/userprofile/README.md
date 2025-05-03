# UserProfile モジュール

このドキュメントでは、`userprofile` モジュールの責務、ドメインモデル、主要な機能について説明します。

## 責務

`userprofile` モジュールは、ユーザーのプロファイル情報と住所の管理に関連する以下の主要な責務を担当します。

*   **ユーザープロファイル管理:**
    *   ユーザーの基本情報 (氏名など) を保存・管理します。
    *   アカウント (`auth` モジュール) と紐づけることでユーザー識別を行います。
*   **住所管理:**
    *   ユーザーの配送先住所情報を複数登録・管理します。
    *   デフォルト住所の設定・変更を行います。
*   **プロファイル情報更新:**
    *   ユーザーのプロファイル情報の更新・変更を処理します。

## ドメインモデル

`userprofile` モジュールの主要なドメインモデルは以下の通りです。

*   **`UserProfile` (Class):**
    *   責務: ユーザープロファイル全体を表す集約ルート。
    *   主要プロパティ:
        *   `id`: ユーザープロファイルID (`UserProfileId` 値オブジェクト)。
        *   `name`: ユーザー名 (String)。
        *   `addresses`: このプロファイルに関連付けられた住所のリスト (`ImmutableList<Address>`)。
        *   `domainEvents`: プロファイルに関連するドメインイベントのリスト (`ImmutableList<DomainEvent>`)。
    *   主要メソッド:
        *   `create`: 新しいユーザープロファイルを作成し、`UserProfileCreated`イベントを発行。
        *   `reconstruct`: 永続化されたユーザープロファイルを再構築（イベントなし）。
        *   `updateName`: ユーザー名を更新し、`UserProfileUpdated`イベントを発行。
        *   `addAddress`: 住所を追加し、`AddressAdded`イベントを発行。
        *   `removeAddress`: 住所を削除し、`AddressRemoved`イベントを発行。
        *   `findDefaultAddress`: デフォルト設定された住所を取得。
        *   `findAddressById`: 指定したIDの住所を取得。

*   **`UserProfileId` (Record, Value Object):**
    *   責務: ユーザープロファイルの一意な識別子。
    *   プロパティ: `id` (UUID)。
    *   メソッド: `of(String)` で文字列からインスタンスを生成。

*   **`Address` (Record, Value Object):**
    *   責務: 住所情報を表す値オブジェクト。
    *   主要プロパティ:
        *   `id`: 住所ID (String)。
        *   `name`: 氏名 (String)。
        *   `postalCode`: 郵便番号 (String)。
        *   `prefecture`: 都道府県 (String)。
        *   `city`: 市区町村 (String)。
        *   `street`: 番地 (String)。
        *   `building`: 建物名・部屋番号 (String, Nullable)。
        *   `phoneNumber`: 電話番号 (String)。
        *   `isDefault`: デフォルト住所フラグ (boolean)。
    *   バリデーション: コンストラクタ内で各フィールドの空チェックを実施。
    *   メソッド: `withDefault(boolean)` でデフォルト設定を変更した新しいインスタンスを生成。

## リポジトリ

`userprofile` モジュールは、クリーンアーキテクチャの原則に従ってリポジトリを設計しています。

*   **`UserProfiles` (Interface):**
    *   責務: ドメイン層で定義されたリポジトリインターフェース。ユーザープロファイル集約の永続化を抽象化します。
    *   主要メソッド:
        *   `findById(UserProfileId id)`: IDでユーザープロファイルを検索。
        *   `findByAccountId(AccountId accountId)`: アカウントIDでユーザープロファイルを検索。
        *   `save(UserProfile userProfile)`: ユーザープロファイルを保存 (新規作成または更新)。

## 主要フロー

`userprofile` モジュールは以下の主要なワークフローを実装しています：

### ユーザープロファイル作成フロー

`CreateUserProfileWorkflow` インターフェースで定義されています。

1.  ユーザー名とアカウントIDを入力として受け取る。
2.  `CreateUserProfileStep` で新しいユーザープロファイルを作成。
3.  `AssociateWithAccountStep` でプロファイルとアカウントを関連付け。
4.  作成されたユーザープロファイルを返却。

### 住所追加フロー

`AddAddressWorkflow` インターフェースで定義されています。

1.  ユーザープロファイルIDと住所情報を入力として受け取る。
2.  `FindUserProfileByIdStep` でユーザープロファイルを検索。
3.  `CreateAddressStep` で新しい住所を作成。
4.  `AddAddressToUserProfileStep` でユーザープロファイルに住所を追加。
5.  更新されたユーザープロファイルを返却。

### ユーザープロファイル更新フロー

`UpdateUserProfileWorkflow` インターフェースで定義されています。

1.  ユーザープロファイルIDと更新情報を入力として受け取る。
2.  ユーザープロファイルを検索。
3.  プロファイル情報を更新。
4.  更新されたユーザープロファイルを返却。

### 住所削除フロー

`RemoveAddressWorkflow` インターフェースで定義されています。

1.  ユーザープロファイルIDと住所IDを入力として受け取る。
2.  ユーザープロファイルを検索。
3.  指定された住所を削除。
4.  更新されたユーザープロファイルを返却。

## 主要コンポーネント

*   **Domain リング:**
    *   `UserProfile`, `UserProfileId`, `Address`: ドメインモデル。
    *   `UserProfiles`: リポジトリインターフェース。
    *   `UserProfileFactory`: ユーザープロファイル作成を担当するドメインサービス。
*   **Application リング:**
    *   `CreateUserProfileWorkflow`: ユーザープロファイル作成処理のワークフローインターフェース。
    *   `AddAddressWorkflow`: 住所追加処理のワークフローインターフェース。
    *   `UpdateUserProfileWorkflow`: プロファイル更新処理のワークフローインターフェース。
    *   `RemoveAddressWorkflow`: 住所削除処理のワークフローインターフェース。
    *   各ワークフロー内の `*Step` インターフェース: 処理ステップのインターフェース。

## 他モジュールとの連携

*   **Auth モジュールとの連携:**
    *   `AccountId` をインポートして、アカウントとユーザープロファイルの紐付けを行います。
    *   `UserProfiles` リポジトリは `findByAccountId` メソッドを提供し、認証済みユーザーの情報取得に使用されます。

*   **Share モジュールとの連携:**
    *   `DomainException` を継承してカスタム例外を作成します。
    *   `IdGenerator` を使用してユーザープロファイルIDを生成します。

## 設計上のポイント

1.  **イミュータブルな設計:**
    *   `Address` は Java Record を使用した不変オブジェクトで、値オブジェクトとして設計。
    *   `UserProfile` は値オブジェクトパターンに従い、状態変更時は新しいインスタンスを生成。

2.  **ビジネスルールのカプセル化:**
    *   空文字列のバリデーション、住所IDの重複チェックなどのビジネスルールをドメインモデル内にカプセル化。
    *   `Address` のコンストラクタ内での入力検証。
    *   `UserProfile` メソッド内での条件チェック (例: `removeAddress` での存在確認)。
    *   デフォルト住所ロジック: 新しいデフォルト住所が追加された場合、既存のデフォルト住所はfalseに変更。

3.  **ドメインイベントの活用:**
    *   ドメインモデルの状態変更時にイベントを発行 (`UserProfileCreated`, `AddressAdded` など)。
    *   これにより、将来的にイベント駆動型アーキテクチャへの拡張が容易に。

4.  **リアクティブプログラミング:**
    *   リポジトリ操作やワークフロー実行は `Mono` を返し、リアクティブなデータフローを実現。