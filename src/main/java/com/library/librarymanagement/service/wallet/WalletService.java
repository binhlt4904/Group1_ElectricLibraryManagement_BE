package com.library.librarymanagement.service.wallet;

import com.library.librarymanagement.dto.response.WalletResponse;

public interface WalletService {
    WalletResponse getWalletByUserId(Long userId);
}
