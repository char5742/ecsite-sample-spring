# Makefile for ECサイト Spring Boot プロジェクト

# 色付き出力用の変数
GREEN := \033[0;32m
RED := \033[0;31m
BLUE := \033[0;34m
NC := \033[0m # No Color

# デフォルトターゲット
.DEFAULT_GOAL := help

# ヘルプメッセージ
.PHONY: help
help: ## このヘルプメッセージを表示
	@echo "$(BLUE)ECサイト Spring Boot プロジェクト - Makefile$(NC)"
	@echo ""
	@echo "利用可能なコマンド:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(GREEN)%-20s$(NC) %s\n", $$1, $$2}'

# Docker Compose コマンド
.PHONY: up
up: ## Docker Compose で全サービスを起動
	@echo "$(BLUE)Starting all services...$(NC)"
	docker compose up -d
	@echo "$(GREEN)All services started successfully!$(NC)"

.PHONY: down
down: ## Docker Compose で全サービスを停止
	@echo "$(BLUE)Stopping all services...$(NC)"
	docker compose down
	@echo "$(GREEN)All services stopped!$(NC)"

.PHONY: restart
restart: down up ## 全サービスを再起動

.PHONY: logs
logs: ## 全サービスのログを表示
	docker compose logs -f

.PHONY: logs-app
logs-app: ## アプリケーションのログのみ表示
	docker compose logs -f app

.PHONY: build
build: ## 全サービスをビルド
	@echo "$(BLUE)Building all services...$(NC)"
	docker compose build
	@echo "$(GREEN)Build completed!$(NC)"

.PHONY: rebuild
rebuild: ## キャッシュなしで全サービスを再ビルド
	@echo "$(BLUE)Rebuilding all services without cache...$(NC)"
	docker compose build --no-cache
	@echo "$(GREEN)Rebuild completed!$(NC)"

# アプリケーション管理
.PHONY: app-shell
app-shell: ## アプリケーションコンテナにシェル接続
	docker compose exec app /bin/sh

.PHONY: app-logs
app-logs: ## アプリケーションのログを表示
	docker compose logs -f app

.PHONY: app-restart
app-restart: ## アプリケーションのみ再起動
	docker compose restart app

# データベース管理
.PHONY: db-shell
db-shell: ## MongoDBシェルに接続
	docker compose exec mongodb mongosh -u admin -p password --authenticationDatabase admin

.PHONY: db-logs
db-logs: ## MongoDBのログを表示
	docker compose logs -f mongodb

.PHONY: db-backup
db-backup: ## データベースをバックアップ
	@echo "$(BLUE)Creating database backup...$(NC)"
	@mkdir -p backups
	@docker compose exec mongodb mongodump -u admin -p password --authenticationDatabase admin --out /tmp/backup
	@docker cp $$(docker compose ps -q mongodb):/tmp/backup ./backups/backup-$$(date +%Y%m%d-%H%M%S)
	@echo "$(GREEN)Backup completed!$(NC)"

# テスト実行
.PHONY: test
test: ## API テストを実行
	@echo "$(BLUE)Running API tests...$(NC)"
	docker compose run --rm api-tests
	@echo "$(GREEN)Test completed!$(NC)"

.PHONY: test-auth
test-auth: ## 認証関連のテストのみ実行
	docker compose run --rm api-tests /bin/sh -c "cd /app/test && ./scripts/run-tests-by-tag.sh auth"

.PHONY: test-specific
test-specific: ## 特定のテストファイルを実行（例: make test-specific FILE=api-tests/auth/login.yml）
	@if [ -z "$(FILE)" ]; then \
		echo "$(RED)Error: FILE パラメータが必要です$(NC)"; \
		echo "使用例: make test-specific FILE=api-tests/auth/login.yml"; \
		exit 1; \
	fi
	docker compose run --rm api-tests /bin/sh -c "cd /app/test && ./scripts/run-specific-test.sh $(FILE)"

# 開発支援
.PHONY: dev
dev: ## 開発環境を起動（ログ表示あり）
	docker compose up

.PHONY: clean
clean: ## 停止してデータも削除（注意：データが失われます）
	@echo "$(RED)Warning: This will delete all data!$(NC)"
	@read -p "Continue? [y/N] " confirm && [ "$$confirm" = "y" ] || exit 1
	docker compose down -v
	@echo "$(GREEN)Cleanup completed!$(NC)"

.PHONY: status
status: ## サービスの状態を表示
	@echo "$(BLUE)Service status:$(NC)"
	@docker compose ps

.PHONY: env-setup
env-setup: ## 環境変数ファイルをセットアップ
	@if [ ! -f .env ]; then \
		echo "$(BLUE)Creating .env file from template...$(NC)"; \
		cp .env.example .env; \
		echo "$(GREEN).env file created! Please edit it with your settings.$(NC)"; \
	else \
		echo "$(BLUE).env file already exists.$(NC)"; \
	fi

# 初回セットアップ
.PHONY: setup
setup: env-setup build up ## 初回セットアップ（環境変数設定、ビルド、起動）
	@echo "$(GREEN)Setup completed!$(NC)"
	@echo "Application is running at: http://localhost:8080"

# ローカル開発（Docker未使用）
.PHONY: local-run
local-run: ## ローカルでアプリケーションを実行（Gradlew使用）
	./gradlew bootRun

.PHONY: local-test
local-test: ## ローカルでユニットテストを実行
	./gradlew test

.PHONY: local-build
local-build: ## ローカルでアプリケーションをビルド
	./gradlew build