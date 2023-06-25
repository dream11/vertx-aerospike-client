package io.d11.aerospike;

public class Constants {
  public static final String NAMESPACE = "NAMESPACE";
  public static final String TEST_NAMESPACE = "test";
  public static final String TEST_SET = "testset";
  public static final String AEROSPIKE_HOST = "aerospike.host";
  public static final String AEROSPIKE_PORT = "aerospike.port";
  public static final String AEROSPIKE_IMAGE_KEY = "aerospike.image";
  public static final String DEFAULT_AEROSPIKE_IMAGE = "aerospike/aerospike-server:6.0.0.0";
  public static final String INIT_DATA_PATH = "src/test/resources/init.aql";
  public static final String INIT_DATA_PATH_IN_CONTAINER = "/aerospike-seed/init.aql";
}
