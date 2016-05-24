package au.com.acegi.sbedao;

import org.agrona.sbe.CompositeDecoderFlyweight;
import org.agrona.sbe.CompositeEncoderFlyweight;
import org.agrona.sbe.MessageDecoderFlyweight;
import org.agrona.sbe.MessageEncoderFlyweight;
import org.agrona.sbe.CompositeStructure;
import org.agrona.sbe.MessageStructure;

/**
 * PoC for a simple SBE-aware DAO.
 * <p>
 * This DAO is not provided to reflect a real DAO, but simply to exercise the
 * proposed interfaces. A lot of important issues are ignored (threading,
 * transactions, error handling etc).
 * <p>
 * The key idea is to present an "easy" persistence API to clients. This means:
 * 1. Type safe, including no casting
 * 2. Allocation free and zero copy compatible (esp for LMDB-based use case)
 * 3. Buffer lifecycle hidden from client
 * 4. Message header handling is hidden from client
 * 5. Feasible for runtime code generation
 */
public interface Dao<K extends CompositeStructure, KE extends CompositeEncoderFlyweight<K>, KD extends CompositeDecoderFlyweight<K>, V extends MessageStructure, VE extends MessageEncoderFlyweight<V>, VD extends MessageDecoderFlyweight<V>> {

  // Methods to fetch flyweights the caller must use with get/put calls
  KE getKeyFlyweight();

  VE getValFlyweight();

  // Methods that include a flyweight argument
  void put(KE key, VE val);

  VD get(KE key);

  // Methods that do not require a flyweight argument
  KD someKey();
}
