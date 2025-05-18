# Runnテスト実行用のDockerイメージ
FROM ghcr.io/k1low/runn:latest

# 必要な環境変数を設定
ENV API_BASE_URL=http://host.docker.internal:8080

# 作業ディレクトリを設定
WORKDIR /tests

# デフォルトコマンド
CMD ["run", "api-tests/**/*.yml"]