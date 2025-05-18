#!/bin/bash
# API テスト実行スクリプト

# スクリプトが配置されているディレクトリに移動
cd "$(dirname "$0")/.."

# 環境設定
export API_BASE_URL="${API_BASE_URL:-http://localhost:8080}"

# カラー出力の設定
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 ECサイト API テスト開始${NC}"
echo "Base URL: $API_BASE_URL"
echo ""

# テスト実行関数
run_test() {
    local test_file=$1
    local test_name=$(basename "$test_file" .yml)
    
    echo -e "${BLUE}📝 実行中: $test_name${NC}"
    if runn run "$test_file"; then
        echo -e "${GREEN}✅ 成功: $test_name${NC}"
        echo ""
        return 0
    else
        echo -e "${RED}❌ 失敗: $test_name${NC}"
        echo ""
        return 1
    fi
}

# テスト結果の集計
total_tests=0
failed_tests=0
failed_test_names=()

# 全テスト実行
for test_file in api-tests/**/*.yml; do
    if [ -f "$test_file" ]; then
        ((total_tests++))
        if ! run_test "$test_file"; then
            ((failed_tests++))
            failed_test_names+=("$(basename "$test_file" .yml)")
        fi
    fi
done

# 結果サマリー
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}テスト結果サマリー${NC}"
echo -e "${BLUE}========================================${NC}"
echo "合計テスト: $total_tests"
echo -e "${GREEN}成功: $((total_tests - failed_tests))${NC}"
echo -e "${RED}失敗: $failed_tests${NC}"

if [ ${#failed_test_names[@]} -gt 0 ]; then
    echo -e "\n${RED}失敗したテスト:${NC}"
    for test in "${failed_test_names[@]}"; do
        echo "  - $test"
    done
fi

echo ""

# 終了コード
if [ $failed_tests -eq 0 ]; then
    echo -e "${GREEN}🎉 全てのテストが成功しました！${NC}"
    exit 0
else
    echo -e "${RED}⚠️  $failed_tests 個のテストが失敗しました${NC}"
    exit 1
fi