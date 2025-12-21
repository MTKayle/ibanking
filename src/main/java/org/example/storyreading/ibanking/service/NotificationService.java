package org.example.storyreading.ibanking.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.example.storyreading.ibanking.entity.User;
import org.example.storyreading.ibanking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    @Autowired
    private UserRepository userRepository;

    /**
     * Đăng ký/Cập nhật FCM token cho user
     * Mỗi user chỉ có 1 device token tại 1 thời điểm
     */
    @Transactional
    public void registerFcmToken(Long userId, String fcmToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

    /**
     * Xóa FCM token khi user đăng xuất
     */
    @Transactional
    public void removeFcmToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        user.setFcmToken(null);
        userRepository.save(user);
    }

    /**
     * Gửi thông báo biến động số dư khi nhận tiền
     */
    public void sendBalanceChangeNotification(Long receiverUserId, BigDecimal amount,
                                               String senderName, String transactionCode,
                                               BigDecimal newBalance) {
        User receiver = userRepository.findById(receiverUserId).orElse(null);

        if (receiver == null || receiver.getFcmToken() == null || receiver.getFcmToken().isEmpty()) {
            System.out.println("User không có FCM token, không thể gửi thông báo");
            return;
        }

        try {
            // Format số tiền theo VND
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            String formattedAmount = currencyFormat.format(amount);
            String formattedBalance = currencyFormat.format(newBalance);

            String title = "Biến động số dư";
            String body = String.format("Bạn vừa nhận %s từ %s. Số dư hiện tại: %s",
                    formattedAmount, senderName, formattedBalance);

            // Tạo data payload
            Map<String, String> data = new HashMap<>();
            data.put("type", "BALANCE_CHANGE");
            data.put("transactionCode", transactionCode);
            data.put("amount", amount.toString());
            data.put("senderName", senderName);
            data.put("newBalance", newBalance.toString());

            sendNotification(receiver.getFcmToken(), title, body, data);

            System.out.println("Đã gửi thông báo biến động số dư cho user: " + receiverUserId);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi thông báo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gửi thông báo nạp tiền thành công (VNPay)
     */
    public void sendDepositNotification(Long userId, BigDecimal amount,
                                         String transactionCode, BigDecimal newBalance) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null || user.getFcmToken() == null || user.getFcmToken().isEmpty()) {
            return;
        }

        try {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            String formattedAmount = currencyFormat.format(amount);
            String formattedBalance = currencyFormat.format(newBalance);

            String title = "Nạp tiền thành công";
            String body = String.format("Bạn vừa nạp %s vào tài khoản. Số dư hiện tại: %s",
                    formattedAmount, formattedBalance);

            Map<String, String> data = new HashMap<>();
            data.put("type", "DEPOSIT");
            data.put("transactionCode", transactionCode);
            data.put("amount", amount.toString());
            data.put("newBalance", newBalance.toString());

            sendNotification(user.getFcmToken(), title, body, data);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi thông báo nạp tiền: " + e.getMessage());
        }
    }

    /**
     * Gửi thông báo chung
     */
    public void sendNotification(String fcmToken, String title, String body, Map<String, String> data) {
        try {
            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());

            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            Message message = messageBuilder.build();
            String response = firebaseMessaging.send(message);

            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("Error sending FCM message: " + e.getMessage());

            // Nếu token không hợp lệ, có thể xóa token khỏi database
            if (e.getMessagingErrorCode() != null) {
                switch (e.getMessagingErrorCode()) {
                    case UNREGISTERED:
                    case INVALID_ARGUMENT:
                        System.out.println("FCM token không hợp lệ, cần xóa khỏi database");
                        break;
                    default:
                        break;
                }
            }
        }
    }
}

