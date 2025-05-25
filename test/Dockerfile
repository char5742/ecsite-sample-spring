# Runnテスト実行用のDockerイメージ
FROM ghcr.io/k1low/runn:latest

# 作業ディレクトリを設定
WORKDIR /tests

# エントリーポイントスクリプトを作成
RUN echo '#!/bin/sh' > /entrypoint.sh && \
    echo 'sleep 30' >> /entrypoint.sh && \
    echo 'exec runn "$@"' >> /entrypoint.sh && \
    chmod +x /entrypoint.sh

# エントリーポイントを設定
ENTRYPOINT ["/entrypoint.sh"]

# デフォルトコマンド
CMD ["run", "/tests/api-tests/**/*.yml"]