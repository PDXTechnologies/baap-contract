package life.pdx.dapp.sample.db.util;


import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;


public final class JacksonUtils {
	  
		private static final ObjectMapper mapper;

		 static {
			mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true); // Important for Token validation.
		}

    /**
     * Serializes an object to JSON.
     *
     * @param value the object to serialize
     * @return the serialized object in JSON
     */    
    public static String toJson(Object ob){
			try {
				return mapper.writeValueAsString(ob);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
    }
    
    /**
     * Deserializes JSON to a Java object.
     *
     * @param json  the object in JSON
     * @param clazz the class of the object to deserialize
     * @return the deserialized object
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
				try {
					return mapper.readValue(json, classOfT);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
    }
    
    /**
     * Deserializes JSON to a Java object.
     *
     * @param json  the byte[] in JSON
     * @param clazz the class of the object to deserialize
     * @return the deserialized object
     */
    public static <T> T fromJson(byte[] json, Class<T> classOfT) throws IOException {
			return mapper.readValue(json, classOfT);
}
    /**
     * Deserializes JSON to a generic Java object .
     *
     * @param json              the object in JSON
     * @param parametrizedClass the generic class of the object to deserialize
     * @param parameterTypes    the parameter classes of parametrized class
     * @return deserialized object
     */
    public static <T> T fromJsonAsParametrized(String json, Class<T> parametrizedClass, Class<?>... parameterTypes) throws IOException {
        JavaType parametricType = constructParametricType(parametrizedClass, parameterTypes);
        return mapper.readValue(json, parametricType);
    }

    /**
     * Deserializes JSON to a generic Java object .
     *
     * @param json              the object in JSON
     * @param parametrizedClass the generic class of the object to deserialize
     * @param parameterTypes    the parameter classes of parametrized class
     * @return deserialized object
     */
    public static <T> T fromJsonAsParametrized(byte[] json, Class<T> parametrizedClass, Class<?>... parameterTypes) throws IOException {
        JavaType parametricType = constructParametricType(parametrizedClass, parameterTypes);
        return mapper.readValue(json, parametricType);
    }
    
    /**
     * Deserializes JSON to a generic Java object .
     *
     * @param json              the object in JSON
     * @param parametrizedClass the generic class of the object to deserialize
     * @param parameterTypes    the parameter classes of parametrized class
     * @return deserialized object
     */
    public static <T> T fromJsonAsParametrized(String json, Class<T> parametrizedClass, JavaType... parameterTypes) throws IOException {
        if (parameterTypes != null && parameterTypes.length > 0) {
            JavaType parametricType = constructParametricType(parametrizedClass, parameterTypes);
            return mapper.readValue(json, parametricType);
        }
        return mapper.readValue(json, parametrizedClass);
    }

    private static TypeFactory getTypeFactory() {
        return mapper.getTypeFactory();
    }

    /**
     * Constructs a parametric type.
     *
     * @param parametrizedClass the parametrized class
     * @param parameterTypes    the parameter classes
     * @return the parametric java type
     */
    public static <T> JavaType constructParametricType(Class<T> parametrizedClass, JavaType... parameterTypes) {
        return getTypeFactory().constructParametricType(parametrizedClass, parameterTypes);
    }

    public static <T> JavaType constructParametricType(Class<T> parametrizedClass, Class<?>... parameterTypes) {
        return getTypeFactory().constructParametricType(parametrizedClass, parameterTypes);
    }
}

