# APIテスト用 Makefile

# DockerコマンドとDocker Composeコマンドの設定
DOCKER := docker
COMPOSE := $(DOCKER) compose

# すべてのテストを実行
.PHONY: test
test:
	$(COMPOSE) run --rm api-tests

# 特定のテストファイルを実行
# 例: make test-one FILE=api-tests/auth/login.yml
.PHONY: test-one
test-one:
	$(COMPOSE) run --rm one-test $(FILE)

# Dockerイメージをビルド
.PHONY: build
build:
	$(COMPOSE) build

# クリーンアップ
.PHONY: clean
clean:
	$(COMPOSE) down --rmi local --volumes

# ヘルプ表示
.PHONY: help
help:
	@echo "使用可能なコマンド:"
	@echo "  make test          - すべてのAPIテストを実行"
	@echo "  make test-one FILE=<path>  - 特定のテストを実行"
	@echo "  make build         - Dockerイメージをビルド"
	@echo "  make clean         - Dockerイメージとボリュームをクリーンアップ"
	@echo "  make help          - このヘルプを表示"