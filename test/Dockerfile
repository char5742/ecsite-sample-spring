# Runnテスト実行用のDockerイメージ
FROM ghcr.io/k1low/runn:latest

# 作業ディレクトリを設定
WORKDIR /tests

# デフォルトコマンド
CMD ["run", "api-tests/**/*.yml"]