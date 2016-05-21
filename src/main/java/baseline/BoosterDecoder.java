/* Generated SBE (Simple Binary Encoding) message codec */
package baseline;

import org.agrona.DirectBuffer;
import org.agrona.sbe.CompositeDecoderFlyweight;

@javax.annotation.Generated(value = {"baseline.BoosterDecoder"})
@SuppressWarnings("all")
public class BoosterDecoder implements CompositeDecoderFlyweight<BoosterType>
{
    public static final int ENCODED_LENGTH = 2;
    private DirectBuffer buffer;
    private int offset;

    public BoosterDecoder wrap(final DirectBuffer buffer, final int offset)
    {
        this.buffer = buffer;
        this.offset = offset;
        return this;
    }

    public int encodedLength()
    {
        return ENCODED_LENGTH;
    }

    public BoostType boostType()
    {
        return BoostType.get(buffer.getByte(offset + 0));
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

    public short horsePower()
    {
        return ((short)(buffer.getByte(offset + 1) & 0xFF));
    }

}
