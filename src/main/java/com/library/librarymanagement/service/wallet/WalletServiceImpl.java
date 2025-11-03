package com.library.librarymanagement.service.wallet;

import com.library.librarymanagement.dto.response.WalletResponse;
import com.library.librarymanagement.entity.Wallet;
import com.library.librarymanagement.entity.WalletTransaction;
import com.library.librarymanagement.repository.wallet.WalletRepository;
import com.library.librarymanagement.repository.wallet.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService{
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    @Override
    public WalletResponse getWalletByUserId(Long userId) {
        Wallet wallet= walletRepository.findByReaderId(userId).orElse(null);
        if(wallet!=null){
            Double totalPaid = 0.0;
            List<WalletTransaction> walletTransaction = walletTransactionRepository.findByWalletIdAndType(wallet.getId(),"DECREASE");

            for(WalletTransaction wt:walletTransaction){
                totalPaid += Double.parseDouble(wt.getAmount().toString());
            }
            WalletResponse walletResponse=new WalletResponse();
            walletResponse.setId(wallet.getId());
            walletResponse.setStatus(wallet.getStatus());
            walletResponse.setTotalPaid(totalPaid);
            walletResponse.setBalance(wallet.getBalance());
            return walletResponse;
        }
        return null;
    }


}
