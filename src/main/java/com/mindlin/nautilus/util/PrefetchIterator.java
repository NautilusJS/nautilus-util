package com.mindlin.nautilus.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class PrefetchIterator<T> implements Iterator<T> {
	
	public static <T> Iterator<T> wrap(PISupplier<? extends T> source) {
		return new PrefetchIterator<T>() {
			@Override
			protected T doGetNext() {
				return source.get();
			}
		};
	}
	
	public static <T> Iterator<T> untilNull(Supplier<? extends T> source) {
		return new UntilNullIterator<>(source);
	}
	
	static final int STATE_FILLED = 0;
	static final int STATE_EMPTY = 1;
	static final int STATE_DONE = 2;
	private int state = STATE_EMPTY;
	private Object next;
	
	protected abstract T doGetNext() throws NoSuchElementException;
	
	@Override
	public boolean hasNext() {
		if (this.state == STATE_EMPTY) {
			try {
				this.next = this.doGetNext();
				this.state = STATE_FILLED;
			} catch (NoSuchElementException e) {
				this.next = e;
				this.state = STATE_DONE;
			}
		}
		
		return this.state == STATE_FILLED;
	}
	
	@Override
	public T next() throws NoSuchElementException {
		switch (state) {
			case STATE_FILLED: {
				@SuppressWarnings("unchecked")
				T result = (T) this.next;
				this.next = null;
				this.state = STATE_EMPTY;
				return result;
			}
			case STATE_EMPTY:
				try {
					return this.doGetNext();
				} catch (NoSuchElementException e) {
					this.state = STATE_DONE;
					throw e;
				}
			case STATE_DONE: {
				NoSuchElementException nse = new NoSuchElementException();
				nse.initCause((Throwable) this.next);
				throw nse;
			}
			default:
				throw new IllegalStateException();
		}
	}
	
	@FunctionalInterface
	public static interface PISupplier<T> {
		T get() throws NoSuchElementException;
	}
	
	static class UntilNullIterator<T> extends PrefetchIterator<T> {
		protected final Supplier<? extends T> source;

		public UntilNullIterator(Supplier<? extends T> source) {
			this.source = Objects.requireNonNull(source);
		}
		
		@Override
		protected T doGetNext() throws NoSuchElementException {
			T result = this.source.get();
			if (result == null)
				throw new NoSuchElementException();
			return result;
		}
	}
}
