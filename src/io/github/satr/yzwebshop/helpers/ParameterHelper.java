package io.github.satr.yzwebshop.helpers;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class ParameterHelper {

    public static String getString(HttpServletRequest request, String parameterName, List<String> errorMessages) {
        String[] values = request.getParameterValues(parameterName);
        if (values != null && values.length > 0)
            return values[0];
        addParameterNotFoundErrorMessage(parameterName, errorMessages);
        return null;
    }

    public static int getInt(HttpServletRequest request, String parameterName, List<String> errorMessages) {
        String[] values = request.getParameterValues(parameterName);
        if (values == null || values.length == 0 ) {
            addParameterNotFoundErrorMessage(parameterName, errorMessages);
            return 0;
        }
        if (!StringHelper.isInteger(values[0])) {
            errorMessages.add(String.format("\"%s\" expected as a number.", parameterName));
            return 0;
        }
        return Integer.parseInt(values[0]);
    }

    public static double getDouble(HttpServletRequest request, String parameterName, List<String> errorMessages) {
        String[] values = request.getParameterValues(parameterName);
        if (values == null && values.length == 0 ) {
            addParameterNotFoundErrorMessage(parameterName, errorMessages);
            return 0.0;
        }
        if (!StringHelper.isDouble(values[0])) {
            errorMessages.add(String.format("\"%s\" expected as a number.", parameterName));
            return 0.0;
        }
        return Double.parseDouble(values[0]);
    }

    private static void addParameterNotFoundErrorMessage(String parameterName, List<String> errorMessages) {
        errorMessages.add(String.format("\"%s\" not found.", parameterName));
    }
}
