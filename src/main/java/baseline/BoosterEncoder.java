/* Generated SBE (Simple Binary Encoding) message codec */
package baseline;

import org.agrona.MutableDirectBuffer;
import org.agrona.sbe.CompositeEncoderFlyweight;

@javax.annotation.Generated(value = {"baseline.BoosterEncoder"})
@SuppressWarnings("all")
public class BoosterEncoder implements CompositeEncoderFlyweight<BoosterType>
{
    public static final int ENCODED_LENGTH = 2;
    private MutableDirectBuffer buffer;
    private int offset;

    public BoosterEncoder wrap(final MutableDirectBuffer buffer, final int offset)
    {
        this.buffer = buffer;
        this.offset = offset;
        return this;
    }

    public int encodedLength()
    {
        return ENCODED_LENGTH;
    }
    public BoosterEncoder boostType(final BoostType value)
    {
        buffer.putByte(offset + 0, value.value());
        return this;
    }

    public static short horsePowerNullValue()
    {
        return (short)255;
    }

    public static short horsePowerMinValue()
    {
        return (short)0;
    }

    public static short horsePowerMaxValue()
    {
        return (short)254;
    }

    public BoosterEncoder horsePower(final short value)
    {
        buffer.putByte(offset + 1, (byte)value);
        return this;
    }

}
