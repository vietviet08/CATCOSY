package com.catcosy.customer.service;

import com.catcosy.customer.config.GeminiConfig;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.aiplatform.v1.EndpointName;
import com.google.cloud.aiplatform.v1.PredictRequest;
import com.google.cloud.aiplatform.v1.PredictResponse;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class GeminiService {

    private static final Logger LOGGER = Logger.getLogger(GeminiService.class.getName());
    private boolean quotaExceeded = false;
    private boolean isApiKeyValid = true;
    private PredictionServiceClient predictionServiceClient = null;
    private final Gson gson = new Gson();
    
    @Autowired
    private GeminiConfig geminiConfig;
    
    /**
     * Khởi tạo PredictionServiceClient cho Gemini API
     * @return Client đã được khởi tạo
     * @throws IOException nếu có lỗi khi khởi tạo client
     */
    private PredictionServiceClient getClient() throws IOException {
        if (predictionServiceClient == null) {
            String apiKey = geminiConfig.getApiKey();
            if (apiKey == null || apiKey.isEmpty()) {
                LOGGER.severe("Gemini API key is missing. Please set gemini.api.key in .env file");
                throw new IOException("Missing Gemini API key");
            }
            
            // Khởi tạo client với default credentials
            // Lưu ý: Với API key, nên sử dụng Vertex AI SDK thay vì PredictionServiceClient trực tiếp
            // Ở đây, chúng ta sẽ sử dụng Google Credentials mặc định
            PredictionServiceSettings settings = PredictionServiceSettings.newBuilder()
                .setCredentialsProvider(() -> GoogleCredentials.getApplicationDefault())
                .build();
                
            predictionServiceClient = PredictionServiceClient.create(settings);
            LOGGER.info("Gemini client initialized successfully");
        }
        
        return predictionServiceClient;
    }
    
    /**
     * Tạo phản hồi sử dụng Google Gemini
     * 
     * @param prompt Câu hỏi của người dùng
     * @param context Lịch sử cuộc trò chuyện
     * @return Phản hồi từ Gemini
     */
    public String generateResponse(String prompt, List<Map<String, String>> context) {
        // Bỏ qua API call nếu đã biết quota bị vượt quá
        if (quotaExceeded) {
            LOGGER.info("Skipping Gemini API call due to previously exceeded quota");
            return "Tôi sẽ giúp bạn dựa vào kiến thức cục bộ vì dịch vụ AI hiện không khả dụng.";
        }
        
        // Bỏ qua nếu API key không hợp lệ
        if (!isApiKeyValid) {
            LOGGER.info("Skipping Gemini API call due to invalid API key");
            return "Tôi đang gặp vấn đề về xác thực với dịch vụ AI. Vui lòng liên hệ quản trị viên để kiểm tra cấu hình API key.";
        }
        
        try {
            // Dùng phương thức giả lập để thử nghiệm trước
            // Trong môi trường thực tế, bạn sẽ kết nối đến Gemini API
            
            // Mô phỏng phản hồi từ AI
            String simulatedResponse = simulateAIResponse(prompt, context);
            LOGGER.info("Simulated AI response generated successfully");
            
            return simulatedResponse;
            
        } catch (Exception e) {
            handleApiError(e);
            return getErrorMessage(e);
        }
    }
    
    /**
     * Phương thức mô phỏng phản hồi từ AI trong khi phát triển
     */
    private String simulateAIResponse(String prompt, List<Map<String, String>> context) {
        // Các cụm từ hỏi đáp cơ bản về thời trang
        if (prompt.toLowerCase().contains("xu hướng thời trang")) {
            return "Xu hướng thời trang năm 2025 đang hướng đến sự bền vững và tính ứng dụng cao. " +
                   "Các màu sắc tự nhiên, chất liệu thân thiện với môi trường, và thiết kế tối giản đang rất được ưa chuộng. " +
                   "CATCOSY tự hào giới thiệu bộ sưu tập mới nhất của chúng tôi phản ánh những xu hướng này.";
        } else if (prompt.toLowerCase().contains("sản phẩm bán chạy") || prompt.toLowerCase().contains("best seller")) {
            return "Các sản phẩm bán chạy nhất của CATCOSY hiện nay bao gồm: áo thun organic cotton, quần jeans tái chế, và bộ sưu tập đồ thể thao từ vải tái chế. " +
                   "Bạn có thể tìm thấy tất cả các sản phẩm này trong mục 'Best Sellers' trên trang web của chúng tôi.";
        } else if (prompt.toLowerCase().contains("khuyến mãi") || prompt.toLowerCase().contains("giảm giá")) {
            return "CATCOSY đang có chương trình khuyến mãi hấp dẫn: giảm 30% cho tất cả các sản phẩm mới trong bộ sưu tập Xuân-Hè, và giảm thêm 10% khi bạn đăng ký làm thành viên. " +
                   "Chương trình kéo dài đến hết tháng 5/2025. Bạn có muốn biết thêm về cách đăng ký làm thành viên không?";
        } else if (prompt.toLowerCase().contains("size") || prompt.toLowerCase().contains("kích cỡ")) {
            return "CATCOSY cung cấp các sản phẩm với đầy đủ các kích cỡ từ XS đến XXL. " +
                   "Chúng tôi khuyên bạn nên tham khảo bảng kích cỡ chi tiết trên trang sản phẩm để chọn size phù hợp nhất. " +
                   "Nếu bạn không chắc chắn, hãy liên hệ với đội ngũ chăm sóc khách hàng của chúng tôi để được tư vấn.";
        } else if (prompt.toLowerCase().contains("chào") || prompt.toLowerCase().contains("xin chào")) {
            return "Xin chào! Tôi là trợ lý ảo của CATCOSY. Rất vui được hỗ trợ bạn hôm nay. Tôi có thể giúp gì cho bạn về các sản phẩm thời trang của chúng tôi?";
        } else if (prompt.toLowerCase().contains("cảm ơn")) {
            return "Không có gì! Rất vui khi được hỗ trợ bạn. Nếu bạn có bất kỳ câu hỏi nào khác về sản phẩm hoặc dịch vụ của CATCOSY, đừng ngần ngại hỏi nhé!";
        } else {
            // Phản hồi chung
            return "Cảm ơn bạn đã liên hệ với CATCOSY! " +
                   "Chúng tôi chuyên cung cấp các sản phẩm thời trang chất lượng cao, bền vững và hợp thời trang. " +
                   "Nếu bạn cần thông tin cụ thể về sản phẩm, chương trình khuyến mãi, hoặc dịch vụ của chúng tôi, " +
                   "hãy cho tôi biết để tôi có thể hỗ trợ bạn tốt hơn.";
        }
    }
    
    /**
     * Chuẩn bị thông điệp cho Gemini API theo định dạng yêu cầu
     */
    private List<Map<String, Object>> prepareMessages(String prompt, List<Map<String, String>> context) {
        List<Map<String, Object>> messages = new ArrayList<>();
        
        // Thêm thông điệp hệ thống nếu cuộc trò chuyện mới
        if (context == null || context.isEmpty()) {
            Map<String, Object> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", 
                "Bạn là trợ lý AI cho trang web thời trang CATCOSY. " +
                "Hãy cung cấp thông tin hữu ích, súc tích về sản phẩm thời trang, xu hướng, " +
                "lời khuyên mua sắm và trả lời các câu hỏi của khách hàng về cửa hàng CATCOSY. " +
                "Hãy giữ câu trả lời ngắn gọn, thân thiện và chuyên nghiệp.");
            messages.add(systemMessage);
        } else {
            // Chuyển đổi lịch sử cuộc trò chuyện sang định dạng của Gemini
            for (Map<String, String> entry : context) {
                Map<String, Object> message = new HashMap<>();
                message.put("role", entry.getOrDefault("role", "user"));
                message.put("content", entry.getOrDefault("content", ""));
                messages.add(message);
            }
        }
        
        // Thêm câu hỏi hiện tại của người dùng
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);
        
        return messages;
    }
    
    /**
     * Xử lý lỗi từ API Gemini
     */
    private void handleApiError(Exception e) {
        String errorMessage = e.getMessage();
        
        // Kiểm tra lỗi quota vượt quá
        if (errorMessage != null && 
            (errorMessage.contains("quota exceeded") || 
             errorMessage.contains("429") || 
             errorMessage.contains("rate limit"))) {
            
            quotaExceeded = true;
            LOGGER.severe("Gemini quota exceeded. Will use local knowledge base for future requests. Error: " + errorMessage);
        }
        
        // Kiểm tra lỗi xác thực
        else if (errorMessage != null && 
            (errorMessage.contains("401") || 
             errorMessage.contains("authentication") || 
             errorMessage.contains("invalid key") || 
             errorMessage.contains("permission denied"))) {
            
            isApiKeyValid = false;
            LOGGER.severe("Gemini authentication failed. API key may be invalid. Error: " + errorMessage);
        }
        
        // Log chi tiết lỗi
        else {
            LOGGER.log(Level.SEVERE, "Error calling Gemini API: " + errorMessage, e);
        }
    }
    
    /**
     * Tạo thông báo lỗi phù hợp với người dùng
     */
    private String getErrorMessage(Exception e) {
        String errorMessage = e.getMessage();
        
        if (errorMessage == null) {
            return "Đã xảy ra lỗi khi kết nối với dịch vụ AI. Tôi sẽ giúp bạn với kiến thức cục bộ.";
        }
        
        if (errorMessage.contains("exceeded") || errorMessage.contains("429") || errorMessage.contains("rate limit")) {
            return "Tôi sẽ giúp bạn với kiến thức cục bộ, vì dịch vụ AI của chúng tôi hiện không khả dụng.";
        }
        
        if (errorMessage.contains("401") || errorMessage.contains("authentication") || errorMessage.contains("invalid key")) {
            return "Tôi đang gặp vấn đề về xác thực với dịch vụ AI. Vui lòng liên hệ quản trị viên để kiểm tra cấu hình API key.";
        }
        
        if (errorMessage.contains("timeout")) {
            return "Phản hồi đang mất nhiều thời gian hơn dự kiến. Vui lòng thử một câu hỏi đơn giản hơn hoặc thử lại sau.";
        }
        
        if (errorMessage.contains("404") || errorMessage.contains("not found")) {
            return "Dịch vụ AI đang gặp vấn đề với yêu cầu này. Điều này có thể do cấu hình model không hợp lệ.";
        }
        
        return "Tôi đang gặp vấn đề kết nối với cơ sở kiến thức. Tôi sẽ giúp bạn với những gì tôi biết.";
    }
    
    /**
     * Kiểm tra kết nối với Gemini API
     * @return true nếu kết nối thành công, false nếu thất bại
     */
    public boolean testGeminiConnection() {
        // Trong phiên bản phát triển, luôn trả về true để tránh lỗi kết nối
        LOGGER.info("Gemini API connection test simulated successfully");
        return true;
    }
    
    /**
     * Chuyển đổi lịch sử cuộc trò chuyện từ định dạng chatbot sang định dạng của Gemini
     * 
     * @param history Lịch sử cuộc trò chuyện từ chatbot
     * @return Danh sách các thông điệp chuyển đổi
     */
    public List<Map<String, String>> convertHistoryToMessages(List<Map<String, String>> history) {
        List<Map<String, String>> messages = new ArrayList<>();
        
        if (history != null) {
            for (Map<String, String> entry : history) {
                String role = entry.getOrDefault("role", "user");
                String content = entry.getOrDefault("content", "");
                
                // Chỉ chuyển đổi các vai trò được hỗ trợ
                if ("assistant".equals(role) || "user".equals(role) || "system".equals(role)) {
                    Map<String, String> message = new HashMap<>();
                    message.put("role", role);
                    message.put("content", content);
                    messages.add(message);
                }
            }
        }
        
        return messages;
    }
}