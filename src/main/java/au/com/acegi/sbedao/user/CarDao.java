package au.com.acegi.sbedao.user;

import au.com.acegi.sbedao.Dao;
import baseline.CarDecoder;
import baseline.CarEncoder;
import baseline.CarStructure;
import baseline.EngineDecoder;
import baseline.EngineEncoder;
import baseline.EngineStructure;

/**
 * Example DAO with enough detail for a runtime code generated implementation.
 * <p>
 * This is all an end user should need to write, along with their SBE schema.
 */
public interface CarDao extends
    Dao<EngineStructure, EngineEncoder, EngineDecoder, CarStructure, CarEncoder, CarDecoder> {

  int MAX_MSG_LEN = 2048;
}
