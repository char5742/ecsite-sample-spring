# APIテスト用 Makefile

# DockerコマンドとDocker Composeコマンドの設定
DOCKER := docker
COMPOSE := $(DOCKER) compose

# デフォルトターゲット
.DEFAULT_GOAL := help

# すべてのテストを実行（ビルドから実行まで）
.PHONY: test
test:
	$(COMPOSE) up --build --exit-code-from api-tests api-tests

# 特定のテストファイルを実行
# 例: make test-one FILE=api-tests/auth/login.yml
.PHONY: test-one
test-one:
	$(COMPOSE) run --rm one-test /tests/$(FILE)

# 環境を起動（バックグラウンド）
.PHONY: up
up:
	$(COMPOSE) up -d app

# 環境を停止
.PHONY: down
down:
	$(COMPOSE) down

# Dockerイメージをビルド
.PHONY: build
build:
	$(COMPOSE) build

# ログを表示
.PHONY: logs
logs:
	$(COMPOSE) logs -f

# クリーンアップ（ボリューム含む）
.PHONY: clean
clean:
	$(COMPOSE) down --rmi local --volumes --remove-orphans

# ヘルプ表示
.PHONY: help
help:
	@echo "使用可能なコマンド:"
	@echo "  make test          - すべてのAPIテストを実行（ビルド含む）"
	@echo "  make test-one FILE=<path>  - 特定のテストを実行"
	@echo "  make up            - 環境をバックグラウンドで起動"
	@echo "  make down          - 環境を停止"
	@echo "  make build         - Dockerイメージをビルド"
	@echo "  make logs          - ログを表示"
	@echo "  make clean         - Dockerイメージとボリュームをクリーンアップ"
	@echo "  make help          - このヘルプを表示"