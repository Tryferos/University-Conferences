package io.github.tryferos.spring_server.Paper;

public class PaperException extends Exception{
    public PaperException(String msg, Object... arg){super(String.format(msg, arg));}
}
