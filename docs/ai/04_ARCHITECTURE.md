# 4. アーキテクチャガイド

このドキュメントでは、`ecsite-v2` プロジェクトのシステムアーキテクチャについて詳細に説明します。

*(現在執筆中です。`01_PROJECT_OVERVIEW.md` の「モジュール設計」「API設計」セクションも参照してください)*

## 全体像

*(システム構成図、リクエストフローなどを記載予定)*

## Spring Modulith

*(モジュール分割の意図、境界、連携方法などを記載予定)*

## リアクティブ (WebFlux & Reactor)

*(ノンブロッキング処理、主要オペレータ、エラーハンドリング、コンテキスト、スケジューラなどを記載予定)*

## API (OpenAPI & SpringDoc)

*(OpenAPI定義、コード生成、Delegateパターン、SpringDoc活用法などを記載予定)*

## データアクセス (MongoDB Reactive)

*(ReactiveMongoRepository, ReactiveMongoTemplate, エンティティ設計などを記載予定)*

## 認証・認可 (Spring Security)

*(SecurityFilterChain, WebFilter, UserDetailsService, メソッドレベルセキュリティなどを記載予定)*

## Null安全性 (JSpecify)

*(`@NonNull`, `@Nullable` の適用ルールなどを記載予定)*
