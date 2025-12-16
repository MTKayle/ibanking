package org.example.storyreading.ibanking.entity;

public enum SavingTerm {
    NON_TERM(0, 0.20),
    ONE_MONTH(1, 3.2),
    TWO_MONTHS(2, 3.4),
    THREE_MONTHS(3, 3.6),
    SIX_MONTHS(6, 4.8),
    NINE_MONTHS(9, 5.0),
    TWELVE_MONTHS(12, 5.5),
    FIFTEEN_MONTHS(15, 5.8),
    EIGHTEEN_MONTHS(18, 6.0),
    TWENTY_FOUR_MONTHS(24, 6.4),
    THIRTY_SIX_MONTHS(36, 6.8);

    private final int months;
    private final double interestRate;

    SavingTerm(int months, double interestRate) {
        this.months = months;
        this.interestRate = interestRate;
    }

    public int getMonths() {
        return months;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public String getDisplayName() {
        if (months == 0) {
            return "Không kỳ hạn";
        }
        return months + " tháng";
    }
}

