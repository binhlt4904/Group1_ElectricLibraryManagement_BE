package com.library.librarymanagement.service.wallet;

import com.library.librarymanagement.dto.response.WalletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WalletService {
    WalletResponse getWalletByUserId(Long userId);


}
