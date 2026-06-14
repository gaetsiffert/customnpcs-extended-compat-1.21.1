package fr.narrnouille.customnpcsextendedcompat.client.render;

import java.lang.reflect.Method;

public final class ShaderShadowPassState {
    private static final String[] STATE_CLASSES = {
            "net.irisshaders.iris.shadows.ShadowRenderingState",
            "net.coderbot.iris.shadows.ShadowRenderingState"
    };
    private static final String[] STATE_METHODS = {
            "areShadowsCurrentlyBeingRendered",
            "isShadowPass",
            "isRenderingShadows"
    };

    private static boolean initialized;
    private static Method shadowPassMethod;

    private ShaderShadowPassState() {
    }

    public static boolean isRenderingShadowPass() {
        Method method = getShadowPassMethod();
        if (method == null) {
            return false;
        }

        try {
            return Boolean.TRUE.equals(method.invoke(null));
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return false;
        }
    }

    private static Method getShadowPassMethod() {
        if (initialized) {
            return shadowPassMethod;
        }

        initialized = true;
        for (String className : STATE_CLASSES) {
            for (String methodName : STATE_METHODS) {
                Method method = findBooleanMethod(className, methodName);
                if (method != null) {
                    shadowPassMethod = method;
                    return shadowPassMethod;
                }
            }
        }
        return null;
    }

    private static Method findBooleanMethod(String className, String methodName) {
        try {
            Method method = Class.forName(className).getMethod(methodName);
            if (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class) {
                return method;
            }
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            return null;
        }
        return null;
    }
}
