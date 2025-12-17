package org.example.storyreading.ibanking.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class để parse VietQR content string
 */
public class VietQRParser {

    /**
     * Parse VietQR content string và trích xuất thông tin
     *
     * @param qrContent VietQR content string
     * @return Map chứa các thông tin đã parse
     */
    public static Map<String, String> parseVietQR(String qrContent) {
        Map<String, String> result = new HashMap<>();

        if (qrContent == null || qrContent.isEmpty()) {
            return result;
        }

        int index = 0;
        while (index < qrContent.length() - 4) {
            // Đọc tag (2 digits)
            String tag = qrContent.substring(index, index + 2);
            index += 2;

            // Đọc length (2 digits)
            String lengthStr = qrContent.substring(index, index + 2);
            index += 2;

            int length;
            try {
                length = Integer.parseInt(lengthStr);
            } catch (NumberFormatException e) {
                break;
            }

            // Đọc value
            if (index + length > qrContent.length()) {
                break;
            }
            String value = qrContent.substring(index, index + length);
            index += length;

            // Parse specific tags
            switch (tag) {
                case "38": // Merchant Account Information
                    parseMerchantInfo(value, result);
                    break;
                case "54": // Transaction Amount
                    result.put("amount", value);
                    break;
                case "59": // Merchant Name
                    result.put("merchantName", value);
                    break;
                case "62": // Additional Data
                    parseAdditionalData(value, result);
                    break;
            }
        }

        return result;
    }

    /**
     * Parse Merchant Account Information (Tag 38)
     */
    private static void parseMerchantInfo(String data, Map<String, String> result) {
        int index = 0;
        while (index < data.length() - 4) {
            String tag = data.substring(index, index + 2);
            index += 2;

            String lengthStr = data.substring(index, index + 2);
            index += 2;

            int length;
            try {
                length = Integer.parseInt(lengthStr);
            } catch (NumberFormatException e) {
                break;
            }

            if (index + length > data.length()) {
                break;
            }
            String value = data.substring(index, index + length);
            index += length;

            switch (tag) {
                case "00": // GUID
                    result.put("guid", value);
                    break;
                case "01": // Bank BIN
                    result.put("bankBin", value);
                    break;
                case "02": // Account Number
                    result.put("accountNumber", value);
                    break;
            }
        }
    }

    /**
     * Parse Additional Data (Tag 62)
     */
    private static void parseAdditionalData(String data, Map<String, String> result) {
        int index = 0;
        while (index < data.length() - 4) {
            String tag = data.substring(index, index + 2);
            index += 2;

            String lengthStr = data.substring(index, index + 2);
            index += 2;

            int length;
            try {
                length = Integer.parseInt(lengthStr);
            } catch (NumberFormatException e) {
                break;
            }

            if (index + length > data.length()) {
                break;
            }
            String value = data.substring(index, index + length);
            index += length;

            if ("08".equals(tag)) { // Purpose of Transaction
                result.put("description", value);
            }
        }
    }

    /**
     * Parse amount string to BigDecimal
     */
    public static BigDecimal parseAmount(String amountStr) {
        if (amountStr == null || amountStr.isEmpty()) {
            return null;
        }

        try {
            return new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

