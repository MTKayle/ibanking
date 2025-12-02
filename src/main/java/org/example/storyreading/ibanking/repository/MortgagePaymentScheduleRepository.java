// ...new file...
package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.MortgageAccount;
import org.example.storyreading.ibanking.entity.MortgagePaymentSchedule;
import org.example.storyreading.ibanking.entity.MortgagePaymentScheduleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MortgagePaymentScheduleRepository extends JpaRepository<MortgagePaymentSchedule, MortgagePaymentScheduleId> {

    List<MortgagePaymentSchedule> findByMortgage(MortgageAccount mortgage);

    List<MortgagePaymentSchedule> findByIdMortgageId(Long mortgageId);

}

