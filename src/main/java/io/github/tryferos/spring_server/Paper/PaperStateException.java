
package io.github.tryferos.spring_server.Paper;

public class PaperStateException extends Exception{
    public PaperStateException(String msg, Object... arg){super(String.format(msg, arg));}
}
