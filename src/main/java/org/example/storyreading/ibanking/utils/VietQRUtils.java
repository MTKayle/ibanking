package org.example.storyreading.ibanking.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;

/**
 * VietQR Standard utility class
 * Generates QR code content following Vietnam QR Payment Standard (VietQR)
 * Based on EMVCo QR Code Specification
 */
public class VietQRUtils {

    // Payload Format Indicator
    private static final String PAYLOAD_FORMAT_INDICATOR = "000201";

    // Point of Initiation Method
    private static final String POINT_OF_INITIATION_DYNAMIC = "010212"; // Dynamic QR (with amount)
    private static final String POINT_OF_INITIATION_STATIC = "010211";  // Static QR (without amount)

    // Merchant Account Information Template for VietQR
    private static final String MERCHANT_ACCOUNT_INFO_TAG = "38";

    // VietQR GUID
    private static final String VIETQR_GUID = "A000000727";

    // Transaction Currency (VND = 704)
    private static final String TRANSACTION_CURRENCY = "5303704";

    // Country Code (Vietnam = VN)
    private static final String COUNTRY_CODE = "5802VN";

    // CRC tag
    private static final String CRC_TAG = "6304";

    /**
     * Generate VietQR content string
     *
     * @param bankBin Bank identification number (BIN code)
     * @param accountNumber Account number
     * @param accountHolderName Account holder name
     * @param amount Transaction amount (null for static QR)
     * @param description Transaction description
     * @return QR code content string
     */
    public static String generateVietQR(String bankBin, String accountNumber,
                                       String accountHolderName, BigDecimal amount,
                                       String description) {
        StringBuilder qrData = new StringBuilder();

        // 1. Payload Format Indicator
        qrData.append(PAYLOAD_FORMAT_INDICATOR);

        // 2. Point of Initiation Method (static if no amount, dynamic if amount specified)
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            qrData.append(POINT_OF_INITIATION_DYNAMIC);
        } else {
            qrData.append(POINT_OF_INITIATION_STATIC);
        }

        // 3. Merchant Account Information (Tag 38 for VietQR)
        qrData.append(buildMerchantAccountInfo(bankBin, accountNumber));

        // 4. Transaction Currency (VND)
        qrData.append(TRANSACTION_CURRENCY);

        // 5. Transaction Amount (if specified)
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            String amountStr = amount.setScale(0, RoundingMode.HALF_UP).toPlainString();
            qrData.append(buildDataObject("54", amountStr));
        }

        // 6. Country Code
        qrData.append(COUNTRY_CODE);

        // 7. Merchant Name (Account Holder Name)
        if (accountHolderName != null && !accountHolderName.isEmpty()) {
            String normalizedName = normalizeVietnameseName(accountHolderName);
            qrData.append(buildDataObject("59", normalizedName));
        }

        // 8. Merchant City (optional - using "HA NOI" as default)
        qrData.append(buildDataObject("60", "HA NOI"));

        // 9. Additional Data Field (for description/purpose)
        if (description != null && !description.isEmpty()) {
            String additionalData = buildDataObject("08", description); // 08 = Purpose of Transaction
            qrData.append(buildDataObject("62", additionalData));
        }

        // 10. CRC (must be calculated last)
        qrData.append(CRC_TAG);
        String crc = calculateCRC(qrData.toString());
        qrData.append(crc);

        return qrData.toString();
    }

    /**
     * Build Merchant Account Information for VietQR
     * Tag 38 contains: GUID, Bank BIN, Account Number
     */
    private static String buildMerchantAccountInfo(String bankBin, String accountNumber) {
        StringBuilder merchantInfo = new StringBuilder();

        // GUID (Tag 00)
        merchantInfo.append(buildDataObject("00", VIETQR_GUID));

        // Beneficiary Organization (Bank BIN + Account Number)
        // Tag 01: Merchant ID (Bank BIN)
        // Tag 02: Account Number
        String beneficiary = buildDataObject("01", bankBin) + buildDataObject("02", accountNumber);
        merchantInfo.append(beneficiary);

        return buildDataObject(MERCHANT_ACCOUNT_INFO_TAG, merchantInfo.toString());
    }

    /**
     * Build a data object with format: [TAG][LENGTH][VALUE]
     * TAG: 2 digits
     * LENGTH: 2 digits (length of VALUE)
     * VALUE: variable length string
     */
    private static String buildDataObject(String tag, String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        int length = value.length();
        String lengthStr = String.format("%02d", length);
        return tag + lengthStr + value;
    }

    /**
     * Calculate CRC-16/CCITT-FALSE checksum
     */
    private static String calculateCRC(String data) {
        String dataWithCRC = data + "0000";
        byte[] bytes;
        try {
            bytes = dataWithCRC.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            bytes = dataWithCRC.getBytes();
        }

        int crc = 0xFFFF;
        int polynomial = 0x1021;

        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i)) & 1) == 1;
                boolean c15 = ((crc >> 15) & 1) == 1;
                crc <<= 1;
                if (c15 ^ bit) {
                    crc ^= polynomial;
                }
            }
        }

        crc &= 0xFFFF;
        return String.format("%04X", crc);
    }

    /**
     * Normalize Vietnamese name by removing diacritics and converting to uppercase
     * VietQR requires ASCII characters only for merchant name
     */
    private static String normalizeVietnameseName(String name) {
        if (name == null) return "";

        String normalized = name.toUpperCase();

        // Replace Vietnamese characters with ASCII equivalents
        normalized = normalized.replaceAll("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]", "A");
        normalized = normalized.replaceAll("[ÈÉẸẺẼÊỀẾỆỂỄ]", "E");
        normalized = normalized.replaceAll("[ÌÍỊỈĨ]", "I");
        normalized = normalized.replaceAll("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]", "O");
        normalized = normalized.replaceAll("[ÙÚỤỦŨƯỪỨỰỬỮ]", "U");
        normalized = normalized.replaceAll("[ỲÝỴỶỸ]", "Y");
        normalized = normalized.replaceAll("Đ", "D");

        // Remove any remaining non-ASCII characters
        normalized = normalized.replaceAll("[^A-Z0-9 ]", "");

        // Limit to 25 characters as per QR standard
        if (normalized.length() > 25) {
            normalized = normalized.substring(0, 25);
        }

        return normalized;
    }
}
