#!/bin/bash

# Đọc API key từ file .env
API_KEY=$(grep "openai.api.key" .env | cut -d'=' -f2)

# Hiển thị phần đầu của API key cho an toàn
API_KEY_SHORT="${API_KEY:0:15}...${API_KEY: -5}"
echo "Sử dụng API key: $API_KEY_SHORT"

# Gọi API OpenAI để kiểm tra kết nối
echo "Đang kiểm tra kết nối đến OpenAI API..."
curl -s -o response.json https://api.openai.com/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $API_KEY" \
  -d '{
    "model": "gpt-3.5-turbo",
    "messages": [
      {
        "role": "system",
        "content": "You are a helpful assistant."
      },
      {
        "role": "user", 
        "content": "Hello, are you working?"
      }
    ],
    "max_tokens": 10
  }'

# Kiểm tra kết quả
if grep -q "error" response.json; then
  echo -e "\n\nKết nối thất bại! Chi tiết lỗi:"
  cat response.json | grep -A 3 "error"
  
  # Phân tích loại lỗi
  if grep -q "exceeded your current quota" response.json; then
    echo -e "\n⚠️ Lỗi quota vượt quá: Bạn đã sử dụng hết quota của tài khoản OpenAI."
    echo "Giải pháp: Kiểm tra thanh toán tại https://platform.openai.com/account/billing"
  elif grep -q "401" response.json || grep -q "invalid_api_key" response.json; then
    echo -e "\n⚠️ Lỗi xác thực: API key không hợp lệ hoặc đã hết hạn."
    echo "Giải pháp: Tạo API key mới tại https://platform.openai.com/api-keys"
  fi
else
  echo -e "\n\nKết nối thành công! Trả lời từ API:"
  cat response.json
fi

# Xóa file tạm
rm response.json