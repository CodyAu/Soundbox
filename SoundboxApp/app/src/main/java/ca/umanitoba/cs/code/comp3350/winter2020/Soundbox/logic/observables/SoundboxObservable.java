package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Extension of java.util.Observable using generics
 */
public abstract class SoundboxObservable<T> {
    private boolean changed = false;
    private final LinkedHashSet<SoundboxObserver<? super T>> observers;

    public SoundboxObservable() {
        this(Collections.emptySet());
    }

    public SoundboxObservable(Collection<SoundboxObserver<? super T>> collection) {
        observers = new LinkedHashSet<>(collection);
    }

    public synchronized void addObserver(final SoundboxObserver<? super T> observer) {
        if (observer == null)
            throw new NullPointerException("can't add null observer");
        observers.add(observer);
    }

    public synchronized void deleteObserver(final SoundboxObserver<? super T> observer) {
        observers.remove(observer);
    }

    public synchronized void deleteObservers() {
        this.observers.clear();
    }

    public synchronized void setChanged() {
        this.changed = true;
    }

    public synchronized void clearChanged() {
        this.changed = false;
    }

    public synchronized boolean hasChanged() {
        return this.changed;
    }

    public synchronized int countObservers() {
        return observers.size();
    }

    public void notifyObservers() {
        notifyObservers(null);
    }

    public void notifyObservers(final T value) {
        if (!changed) return;

        Set<SoundboxObserver<? super T>> toNotify;
        synchronized (this) {
            @SuppressWarnings("unchecked")
            Set<SoundboxObserver<? super T>> notify = (Set<SoundboxObserver<? super T>>) observers.clone(); // Grab observers to notify before allowing changes again
            toNotify = notify;
        }
        changed = false;

        for (SoundboxObserver<? super T> observer : toNotify) {
            observer.update(this, value);
        }
    }

    public abstract void setValue(T value);

    public abstract T getValue();

}
