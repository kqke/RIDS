package huji.impl.ViewChangeAble.messages;

import huji.message.Message;

import java.util.Map;

public class ViewAbleMessage<T extends Comparable<T>> extends Message<T> {
    public final int storage;
    public final int view;

    public final ViewAbleType type;

    public ViewAbleMessage(int from, int to, T body, boolean isClient, int view, int storage, ViewAbleType type) {
        super(from, to, body, isClient);
        this.view = view;
        this.storage = storage;
        this.type = type;
    }

    public ViewAbleMessage(int from, int to, T body, int view, int storage, ViewAbleType type) {
        this(from, to, body, false, view, storage, type);
    }

    public ViewAbleMessage(int from, T body, int view, int storage, ViewAbleType type) {
        this(from, -1, body, false, view, storage, type);
    }

    public ViewAbleMessage(ViewAbleMessage<T> other, int to) {
        super(other, to);
        this.view = other.view;
        this.storage = other.storage;
        this.type = other.type;
    }

    @Override
    public ViewAbleMessage<T> copy(int to) {
        return new ViewAbleMessage<>(this, to);
    }

    @Override
    public ViewAbleMessage<T> add_property(String key, Object value) {
        super.add_property(key, value);
        return this;
    }

    @Override
    public ViewAbleMessage<T> add_properties(Map<String, Object> values) {
        super.add_properties(values);
        return this;
    }
}