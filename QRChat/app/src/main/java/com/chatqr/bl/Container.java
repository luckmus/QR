package com.chatqr.bl;

public class Container<T> {
    private T value;

    public Container() {
    }

    public Container(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
