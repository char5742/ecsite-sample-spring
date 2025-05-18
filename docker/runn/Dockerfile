# Runn テスト実行用 Dockerfile
FROM alpine:3.19

# 必要なパッケージのインストール
RUN apk add --no-cache \
    bash \
    curl \
    jq \
    ca-certificates

# Runn のインストール
RUN curl -L https://github.com/k1LoW/runn/releases/latest/download/runn_$(uname -s)_$(uname -m).tar.gz | tar xz -C /usr/local/bin

# 作業ディレクトリの設定
WORKDIR /app

# エントリーポイント
ENTRYPOINT ["runn"]
CMD ["run", "test/api-tests/**/*.yml"]