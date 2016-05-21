/* Generated SBE (Simple Binary Encoding) message codec */
package baseline;

import org.agrona.DirectBuffer;
import org.agrona.sbe.CompositeDecoderFlyweight;

@javax.annotation.Generated(value = {"baseline.OptionalExtrasDecoder"})
@SuppressWarnings("all")
public class OptionalExtrasDecoder implements CompositeDecoderFlyweight<OptionalExtrasType>
{
    public static final int ENCODED_LENGTH = 1;
    private DirectBuffer buffer;
    private int offset;

    public OptionalExtrasDecoder wrap(final DirectBuffer buffer, final int offset)
    {
        this.buffer = buffer;
        this.offset = offset;
        return this;
    }

    public int encodedLength()
    {
        return ENCODED_LENGTH;
    }

    public boolean sunRoof()
    {
        return 0 != (buffer.getByte(offset) & (1 << 0));
    }

    public boolean sportsPack()
    {
        return 0 != (buffer.getByte(offset) & (1 << 1));
    }

    public boolean cruiseControl()
    {
        return 0 != (buffer.getByte(offset) & (1 << 2));
    }
}
