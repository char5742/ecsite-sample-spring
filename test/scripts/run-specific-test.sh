#!/bin/bash
# 特定のテストを実行するスクリプト

# 使用方法を表示
usage() {
    echo "使用方法: $0 <test-file-path>"
    echo "例: $0 api-tests/auth/login.yml"
    exit 1
}

# 引数チェック
if [ $# -ne 1 ]; then
    usage
fi

# スクリプトが配置されているディレクトリに移動
cd "$(dirname "$0")/.."

# テストファイルのパス
TEST_FILE=$1

# 環境設定
export API_BASE_URL="${API_BASE_URL:-http://localhost:8080}"

# カラー出力の設定
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# テストファイルの存在確認
if [ ! -f "$TEST_FILE" ]; then
    echo -e "${RED}エラー: テストファイルが見つかりません: $TEST_FILE${NC}"
    exit 1
fi

echo -e "${BLUE}🚀 テスト実行開始${NC}"
echo "Test file: $TEST_FILE"
echo "Base URL: $API_BASE_URL"
echo ""

# テスト実行
if runn run "$TEST_FILE" --verbose; then
    echo -e "\n${GREEN}✅ テスト成功${NC}"
    exit 0
else
    echo -e "\n${RED}❌ テスト失敗${NC}"
    exit 1
fi