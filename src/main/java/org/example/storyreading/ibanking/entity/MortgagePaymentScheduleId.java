// ...new file...
package org.example.storyreading.ibanking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MortgagePaymentScheduleId implements Serializable {

    @Column(name = "mortgage_id")
    private Long mortgageId;

    @Column(name = "installment_number")
    private Integer installmentNumber;

    public MortgagePaymentScheduleId() {
    }

    public MortgagePaymentScheduleId(Long mortgageId, Integer installmentNumber) {
        this.mortgageId = mortgageId;
        this.installmentNumber = installmentNumber;
    }

    public Long getMortgageId() {
        return mortgageId;
    }

    public void setMortgageId(Long mortgageId) {
        this.mortgageId = mortgageId;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MortgagePaymentScheduleId that = (MortgagePaymentScheduleId) o;
        return Objects.equals(mortgageId, that.mortgageId) && Objects.equals(installmentNumber, that.installmentNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mortgageId, installmentNumber);
    }
}

