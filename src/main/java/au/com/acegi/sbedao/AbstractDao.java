package au.com.acegi.sbedao;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.Map;
import org.agrona.MutableDirectBuffer;
import org.agrona.Verify;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.sbe.CompositeDecoderFlyweight;
import org.agrona.sbe.CompositeEncoderFlyweight;
import org.agrona.sbe.CompositeType;
import org.agrona.sbe.MessageDecoderFlyweight;
import org.agrona.sbe.MessageEncoderFlyweight;
import org.agrona.sbe.MessageType;

/**
 * Abstract implementation.
 * <p>
 * This is just a PoC, so we KISS by not being thread safe, using Base64 for our
 * map store, and callers sharing the same flyweight instances. In real
 * implementations you'd need thread local storage or specify an explicit thread
 * contracts, re-point DirectBuffers at memory-mapped locations and so on....
 */
public abstract class AbstractDao<K extends CompositeType, KE extends CompositeEncoderFlyweight<K>, KD extends CompositeDecoderFlyweight<K>, V extends MessageType, VE extends MessageEncoderFlyweight<V>, VD extends MessageDecoderFlyweight<V>>
    implements Dao<K, KE, KD, V, VE, VD> {

  private Map<String, String> map = new HashMap<>();
  private Decoder decoder = Base64.getDecoder();
  private Encoder encoder = Base64.getEncoder();

  private KE keyEncoder;
  private KD keyDecoder;
  private LocalMessageHeaderEncoder hdrEncoder;
  private LocalMessageHeaderDecoder hdrDecoder;
  private VE valEncoder;
  private VD valDecoder;

  private ByteBuffer keyBb;
  private ByteBuffer valBb;
  private MutableDirectBuffer keyMdb;
  private MutableDirectBuffer valMdb;

  protected AbstractDao(int maxValSize) {
    this.keyEncoder = keyEncoder();
    this.keyDecoder = keyDecoder();
    this.hdrEncoder = new LocalMessageHeaderEncoder();
    this.hdrDecoder = new LocalMessageHeaderDecoder();
    this.valEncoder = valEncoder();
    this.valDecoder = valDecoder();

    int totalValSize = LocalMessageHeaderEncoder.ENCODED_LENGTH + maxValSize;
    this.keyBb = ByteBuffer.allocate(keyEncoder.encodedLength());
    this.valBb = ByteBuffer.allocate(totalValSize);
    this.keyMdb = new UnsafeBuffer(this.keyBb);
    this.valMdb = new UnsafeBuffer(this.valBb);

    this.keyEncoder.wrap(this.keyMdb, 0);
    this.keyDecoder.wrap(this.keyMdb, 0);
    this.hdrEncoder.wrap(this.valMdb, 0);
    this.hdrDecoder.wrap(this.valMdb, 0);
    this.valEncoder.wrap(this.valMdb, LocalMessageHeaderEncoder.ENCODED_LENGTH);
    // valDecoder wrapping requires persisted acting block length and version
  }

  @Override
  public void put(KE key, VE val) {
    Verify.notNull(key, "Key");
    Verify.notNull(val, "Value");
    assert System.identityHashCode(key) == System.identityHashCode(keyEncoder);
    assert System.identityHashCode(val) == System.identityHashCode(valEncoder);
    hdrEncoder.blockLength(val.sbeBlockLength());
    hdrEncoder.templateId(val.sbeTemplateId());
    hdrEncoder.schemaId(val.sbeSchemaId());
    hdrEncoder.version(val.sbeSchemaVersion());
    String kStr = encoder.encodeToString(keyBb.array());
    String vStr = encoder.encodeToString(valBb.array());
    map.put(kStr, vStr);
  }

  @Override
  public VD get(KE key) {
    Verify.notNull(key, "Key");
    assert System.identityHashCode(key) == System.identityHashCode(keyEncoder);
    String kStr = encoder.encodeToString(keyBb.array());
    String kVal = map.get(kStr);
    if (kVal == null) {
      return null;
    }
    byte[] val = decoder.decode(kVal);
    this.valMdb.putBytes(0, val);
    int blockLength = hdrDecoder.blockLength();
    int version = hdrDecoder.version();
    valDecoder.wrap(this.valMdb, LocalMessageHeaderDecoder.ENCODED_LENGTH,
                    blockLength, version);
    return valDecoder;
  }

  @Override
  public KE getKeyFlyweight() {
    return keyEncoder;
  }

  @Override
  public VE getValFlyweight() {
    return valEncoder;
  }

  @Override
  public KD someKey() {
    if (map.size() == 0) {
      return null;
    }
    keyMdb.putBytes(0, decoder.decode(map.keySet().iterator().next()));
    return keyDecoder;
  }

  // subclass factory methods; subclasses need not perform any wrapping
  protected abstract KE keyEncoder();

  protected abstract KD keyDecoder();

  protected abstract VE valEncoder();

  protected abstract VD valDecoder();
}
