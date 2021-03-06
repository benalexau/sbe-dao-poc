package au.com.acegi.sbedao;

import org.agrona.DirectBuffer;


class LocalMessageHeaderDecoder
{
    public static final int ENCODED_LENGTH = 8;
    private DirectBuffer buffer;
    private int offset;

    public LocalMessageHeaderDecoder wrap(final DirectBuffer buffer, final int offset)
    {
        this.buffer = buffer;
        this.offset = offset;
        return this;
    }

    public int encodedLength()
    {
        return ENCODED_LENGTH;
    }

    public static int blockLengthNullValue()
    {
        return 65535;
    }

    public static int blockLengthMinValue()
    {
        return 0;
    }

    public static int blockLengthMaxValue()
    {
        return 65534;
    }


    public int blockLength()
    {
        return (buffer.getShort(offset + 0, java.nio.ByteOrder.BIG_ENDIAN) & 0xFFFF);
    }


    public static int templateIdNullValue()
    {
        return 65535;
    }

    public static int templateIdMinValue()
    {
        return 0;
    }

    public static int templateIdMaxValue()
    {
        return 65534;
    }


    public int templateId()
    {
        return (buffer.getShort(offset + 2, java.nio.ByteOrder.BIG_ENDIAN) & 0xFFFF);
    }


    public static int schemaIdNullValue()
    {
        return 65535;
    }

    public static int schemaIdMinValue()
    {
        return 0;
    }

    public static int schemaIdMaxValue()
    {
        return 65534;
    }


    public int schemaId()
    {
        return (buffer.getShort(offset + 4, java.nio.ByteOrder.BIG_ENDIAN) & 0xFFFF);
    }


    public static int versionNullValue()
    {
        return 65535;
    }

    public static int versionMinValue()
    {
        return 0;
    }

    public static int versionMaxValue()
    {
        return 65534;
    }


    public int version()
    {
        return (buffer.getShort(offset + 6, java.nio.ByteOrder.BIG_ENDIAN) & 0xFFFF);
    }

}
