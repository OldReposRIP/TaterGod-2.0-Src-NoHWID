package club.tater.tatergod.features.setting;

public class OnChangedValue {

    private final Object _old;
    private final Object _new;

    public OnChangedValue(Object _old, Object _new) {
        this._old = _old;
        this._new = _new;
    }

    public Object getOld() {
        return this._old;
    }

    public Object getNew() {
        return this._new;
    }
}
