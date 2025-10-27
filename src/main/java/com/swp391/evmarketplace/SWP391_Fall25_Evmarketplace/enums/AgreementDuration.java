package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums;

public enum AgreementDuration {
    SIX_MONTHS(6),
    ONE_YEAR(12);

    private final int months;

    AgreementDuration(int months) {
        this.months = months;
    }

    public int getMonths() {
        return months;
    }

}
