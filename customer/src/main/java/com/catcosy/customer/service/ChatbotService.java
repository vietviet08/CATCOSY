package com.catcosy.customer.service;

import com.catcosy.customer.model.ChatbotRequest;
import com.catcosy.customer.model.ChatbotResponse;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatbotService {

    @Autowired
    private OpenAIService openAIService;

    // Knowledge base for the chatbot
    private final Map<String, String> knowledgeBase = new HashMap<>();
    
    // Predefined suggestion sets
    private final Map<String, List<String>> suggestionSets = new HashMap<>();

    public ChatbotService() {
        initializeKnowledgeBase();
        initializeSuggestions();
    }

    private void initializeKnowledgeBase() {
        // Product information
        knowledgeBase.put("product", "Our CATCOSY collection features a variety of clothing items including tops, bottoms, jackets, and accessories. You can browse our full collection on the Products page.");
        knowledgeBase.put("new arrivals", "We add new items to our collection every week. Check our 'New Arrivals' section on the homepage to see the latest styles.");
        knowledgeBase.put("bestseller", "Our current bestsellers include our signature jackets, premium denim, and graphic t-shirts. You can find them in the featured section of our website.");
        knowledgeBase.put("size", "We offer sizes XS through XXL in most of our clothing items. For detailed measurements, please check our size guide on each product page.");
        knowledgeBase.put("material", "We use high-quality materials for our products. Each item's description includes detailed information about fabrics and care instructions.");
        
        // Shipping information
        knowledgeBase.put("shipping", "We offer standard shipping (3-5 business days), express shipping (1-2 business days), and international shipping options. Shipping costs vary based on location and selected method.");
        knowledgeBase.put("delivery", "Standard delivery takes 3-5 business days within domestic locations. Express shipping is available for 1-2 business day delivery for an additional fee.");
        knowledgeBase.put("track", "You can track your order by logging into your account and viewing your order history, or by using the tracking number provided in your shipping confirmation email.");
        knowledgeBase.put("international", "Yes, we ship internationally to over 50 countries. International shipping typically takes 7-14 business days depending on the destination.");
        
        // Return and refund policy
        knowledgeBase.put("return", "We offer a 30-day return policy for unworn items in original condition with tags attached. You can initiate a return by logging into your account or contacting customer service.");
        knowledgeBase.put("refund", "Refunds are processed within 5-7 business days after we receive your returned items. The amount will be credited back to your original payment method.");
        knowledgeBase.put("exchange", "To exchange an item, please return the original purchase and place a new order for the desired item. This ensures faster processing.");
        
        // Account information
        knowledgeBase.put("account", "You can create an account by clicking on the user icon in the top menu and selecting 'Login' or 'Create Account'. An account allows you to track orders, save payment methods, and more.");
        knowledgeBase.put("login", "You can login to your account using the user icon in the top right corner of the page. If you've forgotten your password, there's a 'Forgot Password' option on the login page.");
        knowledgeBase.put("register", "Registration is quick and easy! Click the user icon in the top menu, select 'Create Account', and fill out the form with your details.");
        
        // Payment information
        knowledgeBase.put("payment", "We accept credit cards (Visa, Mastercard, American Express), PayPal, and bank transfers. All transactions are secure and encrypted.");
        knowledgeBase.put("discount", "We regularly offer seasonal discounts and promotions. Sign up for our newsletter to be informed about upcoming sales and special offers.");
        knowledgeBase.put("voucher", "Voucher codes can be entered during checkout in the 'Promo Code' field. Please note that only one voucher can be used per order.");
        
        // Contact and support
        knowledgeBase.put("contact", "Our customer support team is available Monday to Friday, 9 AM - 5 PM via email at support@catcosy.com or by phone at +1-234-567-8910.");
        knowledgeBase.put("help", "For any assistance, please contact our customer service team at support@catcosy.com or call us at +1-234-567-8910 during business hours.");
        
        // General company info
        knowledgeBase.put("about", "CATCOSY is a fashion retailer founded in 2024, focusing on providing high-quality, sustainable fashion at accessible prices. Our mission is to help customers express themselves through unique style choices.");
        knowledgeBase.put("location", "Our main office is located in Da Nang, Vietnam. We operate primarily online but have boutique stores in selected cities.");
    }

    private void initializeSuggestions() {
        suggestionSets.put("product", Arrays.asList("New arrivals", "Bestsellers", "Size guide", "Materials used", "Product categories"));
        suggestionSets.put("shipping", Arrays.asList("Shipping costs", "Delivery times", "Track my order", "International shipping"));
        suggestionSets.put("return", Arrays.asList("Return policy", "How to return", "Refund process", "Exchange items"));
        suggestionSets.put("account", Arrays.asList("Create account", "Login issues", "Update profile", "View orders", "Reset password"));
        suggestionSets.put("payment", Arrays.asList("Payment methods", "Discounts", "Vouchers", "Payment security"));
        suggestionSets.put("default", Arrays.asList("Products", "Shipping", "Returns", "Account", "Payment methods"));
    }

    public ChatbotResponse processQuery(ChatbotRequest request) {
        String query = request.getMessage().toLowerCase().trim();
        List<Map<String, String>> history = request.getHistory();
        
        // Determine the topic category
        String topic = identifyTopic(query);
        
        // Check if we should use the local knowledge base or OpenAI
        String response;
        boolean useGpt = shouldUseGpt(query, history);
        
        if (useGpt) {
            try {
                // Convert history to ChatGPT format
                List<ChatMessage> chatMessages = openAIService.convertHistoryToMessages(history);
                
                // Get response from OpenAI
                response = openAIService.generateResponse(query, chatMessages);
                
                // Check if the response indicates an API issue
                if (response.contains("help you with what I know locally") ||
                    response.contains("currently unavailable") ||
                    response.contains("trouble connecting")) {
                    
                    // If OpenAI failed, try our local knowledge base instead
                    String localResponse = getResponseForQuery(query);
                    
                    // Only use local response if it has specific information
                    if (!localResponse.contains("I don't have specific information")) {
                        response = localResponse;
                    } else {
                        // Use the advanced fallback system instead of hardcoded responses
                        response = getAdvancedFallbackResponse(query, topic);
                    }
                }
            } catch (Exception e) {
                // Fallback to local knowledge base if OpenAI throws any exception
                response = getResponseForQuery(query);
                
                // If local knowledge base doesn't have specific information, use advanced fallback
                if (response.contains("I don't have specific information")) {
                    response = getAdvancedFallbackResponse(query, topic);
                }
            }
        } else {
            // Get response from local knowledge base
            response = getResponseForQuery(query);
        }
        
        // Get relevant suggestions based on topic
        List<String> suggestions = suggestionSets.getOrDefault(topic, suggestionSets.get("default"));
        
        return ChatbotResponse.builder()
                .response(response)
                .suggestions(suggestions)
                .build();
    }
    
    /**
     * Provides a more intelligent fallback response based on the query and topic
     * when neither OpenAI nor the local knowledge base can provide a specific answer
     */
    private String getAdvancedFallbackResponse(String query, String topic) {
        // Map common fashion-related terms to relevant knowledge base entries
        Map<String, List<String>> topicToKeywords = new HashMap<>();
        
        // Product-related fallbacks
        topicToKeywords.put("product", Arrays.asList("product", "clothing", "collection", "item", "wear", "outfit", 
            "style", "fashion", "trend", "color", "size", "material", "fit", "design"));
            
        // Shipping-related fallbacks
        topicToKeywords.put("shipping", Arrays.asList("shipping", "delivery", "track", "order", "package"));
        
        // Return-related fallbacks
        topicToKeywords.put("return", Arrays.asList("return", "refund", "exchange", "warranty"));
        
        // Account-related fallbacks
        topicToKeywords.put("account", Arrays.asList("account", "profile", "login", "register", "password"));
        
        // Payment-related fallbacks
        topicToKeywords.put("payment", Arrays.asList("payment", "pay", "card", "checkout", "discount", "voucher"));
        
        // Find the best matching knowledge base entry for the query
        String bestResponse = null;
        int bestMatchScore = -1;
        
        for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
            int score = calculateRelevanceScore(query, entry.getKey(), topic, topicToKeywords);
            
            if (score > bestMatchScore) {
                bestMatchScore = score;
                bestResponse = entry.getValue();
            }
        }
        
        // If we found a relevant response with a decent score, use it
        if (bestMatchScore >= 2) {
            return bestResponse;
        }
        
        // Generic but category-specific responses as a last resort
        switch (topic) {
            case "product":
                return "I don't have specific information about that product question, but CATCOSY offers a wide range of clothing items including tops, bottoms, jackets, and accessories. You can browse our full collection on the Products page. Would you like information about a particular category?";
                
            case "shipping":
                return "While I don't have specific details on that shipping query, CATCOSY offers various shipping options including standard shipping (3-5 business days), express shipping (1-2 business days), and international shipping. For more details, check the shipping information on our website.";
                
            case "return":
                return "Regarding your return question, CATCOSY has a 30-day return policy for unworn items in original condition with tags attached. For specific questions about returns, you can contact our customer service team.";
                
            case "account":
                return "For account-related questions, you can manage your profile, orders, and preferences through the account section after logging in. If you're having specific issues with your account, our customer support team is available to assist you.";
                
            case "payment":
                return "CATCOSY accepts various payment methods including credit cards, PayPal, and bank transfers. All transactions are secure and encrypted. If you have specific questions about payment options, please contact our customer service.";
                
            default:
                return "I don't have specific information on that topic. For detailed assistance, please contact our customer service team at support@catcosy.com or browse our FAQ section on the website.";
        }
    }
    
    /**
     * Calculate how relevant a knowledge base entry is to a query
     */
    private int calculateRelevanceScore(String query, String knowledgeBaseKey, String topic, Map<String, List<String>> topicToKeywords) {
        int score = 0;
        
        // Direct keyword match in knowledge base
        if (query.contains(knowledgeBaseKey)) {
            score += 3;
        }
        
        // Knowledge base key contains query keywords
        String[] queryWords = query.split("\\s+");
        for (String word : queryWords) {
            if (word.length() > 3 && knowledgeBaseKey.contains(word)) {
                score += 1;
            }
        }
        
        // Topic match
        if (topic != null && !topic.equals("default")) {
            List<String> topicKeywords = topicToKeywords.get(topic);
            if (topicKeywords != null) {
                for (String keyword : topicKeywords) {
                    if (query.contains(keyword)) {
                        score += 1;
                        break;
                    }
                }
            }
        }
        
        return score;
    }

    /**
     * Determine whether to use OpenAI GPT or the local knowledge base
     */
    private boolean shouldUseGpt(String query, List<Map<String, String>> history) {
        // For short queries, check if they're in our knowledge base first
        if (query.length() < 20) {
            for (String key : knowledgeBase.keySet()) {
                if (query.contains(key)) {
                    return false; // Use local knowledge base
                }
            }
        }
        
        // For common greetings, use local knowledge base
        if (containsAny(query, "hi", "hello", "hey", "hola", "thanks", "thank you", "bye", "goodbye")) {
            return false;
        }
        
        // If we have a longer conversation history, use GPT for continuity
        if (history != null && history.size() > 3) {
            return true;
        }
        
        // For specific fashion advice, trends, or complex queries, use GPT
        if (query.length() > 30 || 
            containsAny(query, "trend", "style advice", "fashion", "recommend", "combination", "outfit", 
                       "how to wear", "popular", "sustainable", "eco-friendly", "comparison")) {
            return true;
        }
        
        // Check if it's a complex question
        if (query.contains("?") && (query.contains(" or ") || query.contains(" vs ") || 
            containsAny(query, "why", "how", "what", "when", "which", "compare", "difference"))) {
            return true;
        }
        
        // Default to local knowledge base for better performance
        return false;
    }

    private String identifyTopic(String query) {
        if (containsAny(query, "product", "item", "clothing", "wear", "clothes", "collection", "catalog", "shop", "buy", "purchase", "new arrival", "bestseller", "size", "material")) {
            return "product";
        } else if (containsAny(query, "ship", "deliver", "track", "order status", "package", "arrival", "international")) {
            return "shipping";
        } else if (containsAny(query, "return", "refund", "exchange", "money back", "damaged", "wrong size", "wrong item")) {
            return "return";
        } else if (containsAny(query, "account", "login", "register", "sign up", "sign in", "profile", "password")) {
            return "account";
        } else if (containsAny(query, "payment", "pay", "credit card", "debit card", "paypal", "bank transfer", "discount", "voucher", "coupon", "promo")) {
            return "payment";
        }
        return "default";
    }

    private String getResponseForQuery(String query) {
        // Try to find an exact match in the knowledge base first
        for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
            if (query.equals(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // If no exact match, search for keywords
        for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
            if (query.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // Handle greetings and common phrases
        if (containsAny(query, "hi", "hello", "hey", "hola", "greetings")) {
            return "Hello! How can I assist you with CATCOSY today?";
        }
        
        if (containsAny(query, "thank", "thanks", "appreciate", "helpful")) {
            return "You're welcome! Is there anything else I can help you with?";
        }
        
        if (containsAny(query, "bye", "goodbye", "see you", "later")) {
            return "Thanks for chatting with CATCOSY Assistant! Come back anytime. Have a great day!";
        }
        
        // Fallback response
        return "I don't have specific information on that topic. For detailed assistance, please contact our customer service team at support@catcosy.com or browse our FAQ section on the website.";
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}