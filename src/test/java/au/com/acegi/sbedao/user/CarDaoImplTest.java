package au.com.acegi.sbedao.user;

import au.com.acegi.sbedao.DaoFactory;
import baseline.BooleanType;
import baseline.BoostType;
import baseline.CarDecoder;
import baseline.CarDecoder.FuelFiguresDecoder;
import baseline.CarDecoder.PerformanceFiguresDecoder;
import baseline.CarDecoder.PerformanceFiguresDecoder.AccelerationDecoder;
import baseline.CarEncoder;
import baseline.EngineDecoder;
import baseline.EngineEncoder;
import baseline.Model;
import baseline.OptionalExtrasDecoder;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class CarDaoImplTest {
  private static final byte[] VEHICLE_CODE;
  private static final byte[] MANUFACTURER_CODE;
  private static final byte[] MAKE;
  private static final byte[] MODEL;
  private static final int MODEL_YEAR = 2013;
  private static final MutableDirectBuffer ACTIVATION_CODE;
  private static final BooleanType AVAILABLE = BooleanType.T;
  private static final Model CODE = Model.A;
  private static final long SN = 12345;

  static {
    try {
      VEHICLE_CODE = "abcdef".getBytes(CarEncoder.vehicleCodeCharacterEncoding());
      MANUFACTURER_CODE = "123".getBytes(EngineEncoder.manufacturerCodeCharacterEncoding());
      MAKE = "Honda".getBytes(CarEncoder.makeCharacterEncoding());
      MODEL = "Civic VTi".getBytes(CarEncoder.modelCharacterEncoding());
      final byte[] code = "abcdef".getBytes(CarEncoder.activationCodeCharacterEncoding());
      ACTIVATION_CODE = new UnsafeBuffer(ByteBuffer.allocate(code.length));
      ACTIVATION_CODE.putBytes(0, code);
    } catch (final UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Test
  public void staticDao() {
    CarDao dao = new CarDaoImpl();
    testWithDao(dao);
  }

  @Test
  public void generatedDao() {
    DaoFactory factory = new DaoFactory();
    CarDao dao = factory.newInstance(CarDao.class);
    testWithDao(dao);
  }

  public void testWithDao(CarDao dao) {
    EngineEncoder key = dao.getKeyFlyweight();
    CarEncoder val = dao.getValFlyweight();

    // get when map empty
    assertThat(dao.someKey(), is(nullValue()));

    int engineCapacity = 12345;
    encodeEngine(key, engineCapacity, 0);
    encodeCar(val, engineCapacity);
    dao.put(key, val);
    CarDecoder readVal = dao.get(key);
    decodeCar(readVal, engineCapacity);

    // random key
    EngineDecoder keyDecoder = dao.someKey();
    assertThat(keyDecoder.capacity(), is(engineCapacity));

    // not found use case
    engineCapacity = 99999;
    encodeEngine(key, engineCapacity, 0);
    readVal = dao.get(key);
    assertThat(readVal, is(nullValue()));

    engineCapacity = 54321;
    encodeEngine(key, engineCapacity, 0);
    encodeCar(val, engineCapacity);
    dao.put(key, val);
    readVal = dao.get(key);
    decodeCar(readVal, engineCapacity);

    engineCapacity = 12345;
    encodeEngine(key, engineCapacity, 0);
    readVal = dao.get(key);
    decodeCar(readVal, engineCapacity);

    engineCapacity = 54321;
    encodeEngine(key, engineCapacity, 0);
    readVal = dao.get(key);
    decodeCar(readVal, engineCapacity);
  }

  private void encodeEngine(EngineEncoder engine, int engineCap, int srcOffset) {
    engine.capacity(engineCap)
        .numCylinders((short) 4)
        .putManufacturerCode(MANUFACTURER_CODE, srcOffset)
        .booster().boostType(BoostType.NITROUS).horsePower((short) 200);
  }

  private void encodeCar(CarEncoder car, int engineCap) {
    final int srcOffset = 0;
    car.serialNumber(SN)
        .modelYear(MODEL_YEAR)
        .available(AVAILABLE)
        .code(CODE)
        .putVehicleCode(VEHICLE_CODE, srcOffset);

    for (int i = 0, size = CarEncoder.someNumbersLength(); i < size; i++) {
      car.someNumbers(i, i);
    }

    car.extras()
        .clear()
        .cruiseControl(true)
        .sportsPack(true)
        .sunRoof(false);

    encodeEngine(car.engine(), engineCap, srcOffset);

    car.fuelFiguresCount(3)
        .next().speed(30).mpg(35.9f).usageDescription("Urban Cycle")
        .next().speed(55).mpg(49.0f).usageDescription("Combined Cycle")
        .next().speed(75).mpg(40.0f).usageDescription("Highway Cycle");

    final CarEncoder.PerformanceFiguresEncoder perfFigures = car.performanceFiguresCount(2);
    perfFigures.next()
        .octaneRating((short) 95)
        .accelerationCount(3)
        .next().mph(30).seconds(4.0f)
        .next().mph(60).seconds(7.5f)
        .next().mph(100).seconds(12.2f);
    perfFigures.next()
        .octaneRating((short) 99)
        .accelerationCount(3)
        .next().mph(30).seconds(3.8f)
        .next().mph(60).seconds(7.1f)
        .next().mph(100).seconds(11.8f);

    car.make(new String(MAKE, StandardCharsets.UTF_8))
        .putModel(MODEL, srcOffset, MODEL.length)
        .putActivationCode(ACTIVATION_CODE, 0, ACTIVATION_CODE.capacity());
  }

  private void decodeCar(CarDecoder car, int engineCap) {
    final byte[] buffer = new byte[128];

    assertThat(car.serialNumber(), is(SN));
    assertThat(car.modelYear(), is(MODEL_YEAR));
    assertThat(car.available(), is(AVAILABLE));
    assertThat(car.code(), is(CODE));

    for (int i = 0, size = CarEncoder.someNumbersLength(); i < size; i++) {
      assertThat((int) car.someNumbers(i), is(i));
    }

    final OptionalExtrasDecoder extras = car.extras();
    assertThat(extras.cruiseControl(), is(true));
    assertThat(extras.sportsPack(), is(true));
    assertThat(extras.sunRoof(), is(false));

    final EngineDecoder engine = car.engine();
    assertThat(engine.capacity(), is(engineCap));

    for (final FuelFiguresDecoder fuelFigures : car.fuelFigures()) {
      assertThat(fuelFigures.count(), is(3));
      fuelFigures.usageDescription();
    }

    for (final PerformanceFiguresDecoder perfFigures : car.performanceFigures()) {
      perfFigures.octaneRating();
      for (AccelerationDecoder acceleration : perfFigures.acceleration()) {
        acceleration.mph();
      }
      assertThat(perfFigures.count(), is(2));
    }

    assertThat(car.make(), is(new String(MAKE, StandardCharsets.UTF_8)));
  }

}
