package org.agrona.sbe;

/**
 * Methods common to both <code>sbe:message</code> encoder and decoder flyweights.
 */
public interface MessageFlyweight<T extends MessageType> extends Flyweight<T>
{
    int sbeBlockLength();

    int sbeTemplateId();

    int sbeSchemaId();

    int sbeSchemaVersion();

    String sbeSemanticType();

    int offset();
}
