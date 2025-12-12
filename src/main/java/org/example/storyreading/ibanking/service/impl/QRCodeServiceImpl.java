package org.example.storyreading.ibanking.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.example.storyreading.ibanking.dto.QRCodeRequest;
import org.example.storyreading.ibanking.dto.QRCodeResponse;
import org.example.storyreading.ibanking.entity.Account;
import org.example.storyreading.ibanking.entity.CheckingAccount;
import org.example.storyreading.ibanking.exception.BadRequestException;
import org.example.storyreading.ibanking.exception.ResourceNotFoundException;
import org.example.storyreading.ibanking.repository.CheckingAccountRepository;
import org.example.storyreading.ibanking.service.QRCodeService;
import org.example.storyreading.ibanking.utils.VietQRUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeServiceImpl implements QRCodeService {

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Value("${vietqr.bank.bin:970422}")
    private String bankBin;

    @Value("${vietqr.bank.name:My iBank}")
    private String bankName;

    @Value("${vietqr.bank.code:MYIBANK}")
    private String bankCode;

    private static final int QR_CODE_WIDTH = 300;
    private static final int QR_CODE_HEIGHT = 300;

    @Override
    @Transactional(readOnly = true)
    public QRCodeResponse generateQRCodeForCheckingAccount(Long userId, QRCodeRequest request) {
        // Find user's checking account
        CheckingAccount checkingAccount = checkingAccountRepository
                .findCheckingAccountsByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản checking"));

        Account account = checkingAccount.getAccount();

        // Validate account status
        if (account.getStatus() != Account.Status.active) {
            throw new BadRequestException("Tài khoản không hoạt động, không thể tạo mã QR");
        }

        // Get account information
        String accountNumber = account.getAccountNumber();
        String accountHolderName = account.getUser().getFullName();

        // Get amount and description from request (optional)
        BigDecimal amount = request.getAmount();
        String description = request.getDescription();

        // Validate amount if provided
        if (amount != null && amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Số tiền phải lớn hơn 0");
        }

        // Generate VietQR content string
        String qrContent = VietQRUtils.generateVietQR(
                bankBin,
                accountNumber,
                accountHolderName,
                amount,
                description
        );

        // Generate QR code image
        String qrCodeBase64;
        try {
            qrCodeBase64 = generateQRCodeImage(qrContent);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Lỗi khi tạo mã QR: " + e.getMessage(), e);
        }

        // Build response
        QRCodeResponse response = new QRCodeResponse();
        response.setQrCodeBase64(qrCodeBase64);
        response.setAccountNumber(accountNumber);
        response.setAccountHolderName(accountHolderName);
        response.setBankCode(bankCode);
        response.setBankName(bankName);
        response.setAmount(amount);
        response.setDescription(description);
        response.setQrContent(qrContent);

        return response;
    }

    /**
     * Generate QR code image from content string
     * @param content QR code content
     * @return Base64 encoded PNG image
     */
    private String generateQRCodeImage(String content) throws WriterException, IOException {
        // Configure QR code hints
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        // Create QR code writer
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE,
                QR_CODE_WIDTH, QR_CODE_HEIGHT, hints);

        // Convert to image
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Convert to Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        byte[] imageBytes = baos.toByteArray();

        return Base64.getEncoder().encodeToString(imageBytes);
    }
}

