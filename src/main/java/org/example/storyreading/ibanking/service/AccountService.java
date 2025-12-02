package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.CheckingAccountInfoResponse;

public interface AccountService {


    CheckingAccountInfoResponse getCheckingAccountInfo(Long userId);

}
