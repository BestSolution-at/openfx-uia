package com.sun.glass.ui.uia;

public class HResultException extends RuntimeException {
    
    public static final int E_INVALIDARG = 0x80070057;
    public static final int E_FAIL = 0x80004005;
    public static final int E_NOTIMPL = 0x80004001;

    private int hresult;

    public HResultException(int hresult) {
        this.hresult = hresult;
    }

    public int getHResult() {
        return hresult;
    }
}
