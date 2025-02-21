package at.bestsolution.uia;

public class HResultException extends RuntimeException {

    // #define SEVERITY_SUCCESS    0
    public static final int SEVERITY_SUCCESS = 0;
    // #define SEVERITY_ERROR      1
    public static final int SEVERITY_ERROR = 1;

    public static final int E_INVALIDARG = 0x80070057;
    public static final int E_FAIL = 0x80004005;
    public static final int E_NOTIMPL = 0x80004001;

    // #define E_JAVAEXCEPTION  MAKE_HRESULT(SEVERITY_ERROR, 0xDE, 1)
    public static final int E_JAVAEXCEPTION = 0x80de0001;

    private int hresult;

    public HResultException(int hresult) {
        this.hresult = hresult;
    }

    // called by native code
    public int getHResult() {
        return hresult;
    }

    // called by native code
    public static boolean isHResultException(Throwable t) {
        return t instanceof HResultException;
    }
}
