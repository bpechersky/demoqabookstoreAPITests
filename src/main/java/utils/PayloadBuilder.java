package utils;

public class PayloadBuilder {
    public static String buildDeletePayload(String isbn, String userId) {
        return "{ \"isbn\": \"" + isbn + "\", \"userId\": \"" + userId + "\" }";
    }
}
