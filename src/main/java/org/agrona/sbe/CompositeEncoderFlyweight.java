package org.agrona.sbe;

import org.agrona.MutableDirectBuffer;

/**
 * A <code>sbe:composite</code> encoder flyweight.
 */
public interface CompositeEncoderFlyweight<T extends CompositeType> extends EncoderFlyweight<T> {
}
