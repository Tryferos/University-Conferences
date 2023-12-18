package io.github.tryferos.spring_server;

public abstract class Stateful {

    protected int state = 0;

    protected int id;
    protected long date;

    public Stateful(int id, int state, long date){
        this.id = id;
        this.date = date;
        this.state = state;
    }
    public Stateful(int id){
        this.id = id;
    }

    public Stateful(){

    }
    protected abstract String getStateText();
    public abstract boolean proceed();
}
