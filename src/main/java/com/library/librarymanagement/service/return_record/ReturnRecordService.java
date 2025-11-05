package com.library.librarymanagement.service.return_record;

import com.library.librarymanagement.dto.response.ReturnRecordResponse;

import java.util.Date;

public interface ReturnRecordService {
    ReturnRecordResponse returnRecord(Long borrowId, String note);
}
