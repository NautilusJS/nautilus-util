package com.mindlin.nautilus.util;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.Nonnull;

public class Pair<A, B> implements Serializable, Cloneable {
	private static final long serialVersionUID = 5873353612499810281L;
	
	protected final A a;
	protected final B b;
	
	/** For serialization */
	@SuppressWarnings("unused")
	private Pair() {
		this(null, null);
	}
	
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	public A left() {
		return this.a;
	}
	
	public B right() {
		return this.b;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Pair<A, B> clone() {
		try {
			return (Pair<A, B>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.left(), this.right());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Pair))
			return false;
		Pair<?, ?> oPair = (Pair<?, ?>) obj;
		return Objects.equals(this.left(), oPair.left())
				&& Objects.equals(this.right(), oPair.right());
	}
	
	@Override
	@Nonnull
	public String toString() {
		return String.format("%s{%s, %s}",
				this.getClass().getName(),
				this.left(),
				this.right());
	}
}
