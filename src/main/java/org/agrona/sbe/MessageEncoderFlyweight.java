package org.agrona.sbe;

import org.agrona.MutableDirectBuffer;

/**
 * An <code>sbe:message</code> encoder flyweight.
 */
public interface MessageEncoderFlyweight<T extends MessageType> extends MessageFlyweight<T>, EncoderFlyweight<T> {
}
