package com.example.sabujak.branch.exception;

import com.example.sabujak.common.exception.CustomException;
import com.example.sabujak.common.exception.ErrorCode;

public class BranchException extends CustomException {

    public BranchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
