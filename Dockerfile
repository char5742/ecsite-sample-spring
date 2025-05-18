# マルチステージビルド
# ステージ1: ビルド
FROM eclipse-temurin:23-jdk-alpine AS build
WORKDIR /app

# Gradle Wrapperと設定ファイルをコピー
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY spotless.gradle .

# 依存関係をダウンロード（キャッシュ効率化）
RUN ./gradlew dependencies --no-daemon

# ソースコードをコピー
COPY src src

# アプリケーションをビルド
RUN ./gradlew build -x test --no-daemon

# ステージ2: 実行環境
FROM eclipse-temurin:23-jre-alpine
WORKDIR /app

# 必要なパッケージをインストール（ヘルスチェック用）
RUN apk add --no-cache curl

# ビルド済みのJARファイルをコピー
COPY --from=build /app/build/libs/*.jar app.jar

# アプリケーション実行ユーザーを作成（セキュリティ向上）
RUN addgroup -g 1000 appgroup && \
    adduser -D -u 1000 -G appgroup appuser

USER appuser

# ポート公開
EXPOSE 8080

# JVMオプションとアプリケーション起動
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "/app/app.jar"]