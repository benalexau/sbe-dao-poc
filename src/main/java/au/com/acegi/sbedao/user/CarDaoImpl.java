package au.com.acegi.sbedao.user;

import au.com.acegi.sbedao.AbstractDao;
import baseline.CarDecoder;
import baseline.CarEncoder;
import baseline.CarType;
import baseline.EngineDecoder;
import baseline.EngineEncoder;
import baseline.EngineType;

/**
 * Example of what we could generate at runtime to implement the DAO.
 */
public class CarDaoImpl extends AbstractDao<EngineType, EngineEncoder, EngineDecoder, CarType, CarEncoder, CarDecoder>
    implements CarDao {

  public CarDaoImpl() {
    super(MAX_MSG_LEN);
  }

  @Override
  protected EngineDecoder keyDecoder() {
    return new EngineDecoder();
  }

  @Override
  protected EngineEncoder keyEncoder() {
    return new EngineEncoder();
  }

  @Override
  protected CarDecoder valDecoder() {
    return new CarDecoder();
  }

  @Override
  protected CarEncoder valEncoder() {
    return new CarEncoder();
  }

}
