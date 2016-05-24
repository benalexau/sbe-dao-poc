/* Generated SBE (Simple Binary Encoding) message codec */
package baseline;

import org.agrona.DirectBuffer;
import org.agrona.sbe.CompositeDecoderFlyweight;

@javax.annotation.Generated(value = {"baseline.EngineDecoder"})
@SuppressWarnings("all")
public class EngineDecoder implements CompositeDecoderFlyweight<EngineStructure>
{
    public static final int ENCODED_LENGTH = 8;
    private DirectBuffer buffer;
    private int offset;

    public EngineDecoder wrap(final DirectBuffer buffer, final int offset)
    {
        this.buffer = buffer;
        this.offset = offset;
        return this;
    }

    public int encodedLength()
    {
        return ENCODED_LENGTH;
    }

    public static int capacityNullValue()
    {
        return 65535;
    }

    public static int capacityMinValue()
    {
        return 0;
    }

    public static int capacityMaxValue()
    {
        return 65534;
    }

    public int capacity()
    {
        return (buffer.getShort(offset + 0, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
    }


    public static short numCylindersNullValue()
    {
        return (short)255;
    }

    public static short numCylindersMinValue()
    {
        return (short)0;
    }

    public static short numCylindersMaxValue()
    {
        return (short)254;
    }

    public short numCylinders()
    {
        return ((short)(buffer.getByte(offset + 2) & 0xFF));
    }


    public static int maxRpmNullValue()
    {
        return 65535;
    }

    public static int maxRpmMinValue()
    {
        return 0;
    }

    public static int maxRpmMaxValue()
    {
        return 65534;
    }

    public int maxRpm()
    {
        return 9000;
    }

    public static byte manufacturerCodeNullValue()
    {
        return (byte)0;
    }

    public static byte manufacturerCodeMinValue()
    {
        return (byte)32;
    }

    public static byte manufacturerCodeMaxValue()
    {
        return (byte)126;
    }

    public static int manufacturerCodeLength()
    {
        return 3;
    }

    public byte manufacturerCode(final int index)
    {
        if (index < 0 || index >= 3)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 3 + (index * 1);
        return buffer.getByte(pos);
    }


    public static String manufacturerCodeCharacterEncoding()
    {
        return "UTF-8";
    }

    public int getManufacturerCode(final byte[] dst, final int dstOffset)
    {
        final int length = 3;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("dstOffset out of range for copy: offset=" + dstOffset);
        }

        buffer.getBytes(this.offset + 3, dst, dstOffset, length);

        return length;
    }


    public static byte fuelNullValue()
    {
        return (byte)0;
    }

    public static byte fuelMinValue()
    {
        return (byte)32;
    }

    public static byte fuelMaxValue()
    {
        return (byte)126;
    }

    private static final byte[] FUEL_VALUE = {80, 101, 116, 114, 111, 108};

    public static int fuelLength()
    {
        return 6;
    }

    public byte fuel(final int index)
    {
        return FUEL_VALUE[index];
    }

    public int getFuel(final byte[] dst, final int offset, final int length)
    {
        final int bytesCopied = Math.min(length, 6);
        System.arraycopy(FUEL_VALUE, 0, dst, offset, bytesCopied);
        return bytesCopied;
    }

    private final BoosterDecoder booster = new BoosterDecoder();

    public BoosterDecoder booster()
    {
        booster.wrap(buffer, offset + 6);
        return booster;
    }
}
