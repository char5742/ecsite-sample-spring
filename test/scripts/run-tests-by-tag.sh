#!/bin/bash
# タグ指定でテストを実行するスクリプト

# 使用方法を表示
usage() {
    echo "使用方法: $0 <tag>"
    echo "例: $0 auth"
    echo "利用可能なタグ: auth, signup, login, userprofile, address, shopping, cart, add-item"
    exit 1
}

# 引数チェック
if [ $# -ne 1 ]; then
    usage
fi

# スクリプトが配置されているディレクトリに移動
cd "$(dirname "$0")/.."

# タグ
TAG=$1

# 環境設定
export API_BASE_URL="${API_BASE_URL:-http://localhost:8080}"

# カラー出力の設定
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 タグ指定テスト実行開始${NC}"
echo "Tag: $TAG"
echo "Base URL: $API_BASE_URL"
echo ""

# テスト実行
if runn run api-tests/**/*.yml --tag "$TAG"; then
    echo -e "\n${GREEN}✅ テスト成功${NC}"
    exit 0
else
    echo -e "\n${RED}❌ テスト失敗${NC}"
    exit 1
fi