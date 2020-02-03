package com.majia.expection;

public class XiangGongException extends RuntimeException{
    public XiangGongException(String msg)
    {
        super(msg);
    }
    public XiangGongException(Throwable throwable)
    {
        super(throwable);
    }
}
