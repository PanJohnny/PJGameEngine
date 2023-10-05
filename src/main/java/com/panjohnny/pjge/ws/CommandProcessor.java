package com.panjohnny.pjge.ws;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.panjohnny.pjge.PJGE;
import com.panjohnny.pjgl.api.PJGL;
import com.panjohnny.pjgl.api.object.Component;
import com.panjohnny.pjgl.api.object.GameObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class CommandProcessor {
    public static final List<Command> COMMANDS = new ArrayList<>();
    private static final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).registerTypeAdapter(GameObject.class, new GameObjectSerializer()).registerTypeAdapter(Component.class, new ComponentSerializer()).create();

    public static String process(String command) {
        Optional<Command> o = COMMANDS.stream().filter(c -> c.matches(command)).findFirst();
        return o.map(value -> value.execute.apply(command.contains(" ") ? command.split(" ") : null)).orElse("ERR NOT_FOUND");
    }

    public static void registerDebuggerCommands() {
        COMMANDS.add(new Command("PJGL.OBJECTS", (ignored) -> {
            JsonArray array = new JsonArray();
            for (GameObject obj : PJGL.getInstance().getManager().getObjects()) {
                array.add(gson.toJsonTree(obj, GameObject.class));
            }

            return "RTR " + array;
        }));

        COMMANDS.add(new Command("PJGL.UPDATE_OBJECT", (args) -> {
            // obj.hash + " " + c.class + " " + k + " " + input.value
            int hash = Integer.parseInt(args[1]);
            String className = args[2];
            String fieldName = args[3];
            String value = args[4];

            PJGE.LOGGER.log(System.Logger.Level.INFO, "Update call {0} {1} {2} {3}", hash, className, fieldName, value);

            for (GameObject forHash : PJGL.getInstance().getManager().getObjects()) {
                if (forHash.hashCode() == hash) {
                    try {
                        Class<?> componentClazz = Class.forName(className);
                        if (Component.class.isAssignableFrom(componentClazz)) {
                            for (Field declaredField : forHash.getClass().getDeclaredFields()) {
                                if (declaredField.getType().isAssignableFrom(componentClazz)) {
                                    Object component = declaredField.get(forHash);

                                    Object fv = value;
                                    // what type is this value?
                                    try {
                                        fv = Integer.parseInt(value);
                                    } catch (NumberFormatException ignored) {
                                        if (value.equals("true") || value.equals("false"))
                                            fv = Boolean.parseBoolean(value);
                                        else if (value.equals("on") || value.equals("off"))
                                            fv = value.equals("on");
                                    }

                                    component.getClass().getField(fieldName).set(component, fv);
                                    return "RTR SUCCESS";
                                }
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return "ERR CLASS_NOT_FOUND";
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return "ERR ILLEGAL_ACCESS";
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                        return "ERR NO_SUCH_FIELD";
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "ERR :(";
                    }
                }
            }
            return "ERR FAILED";
        }));

        registerCommonCommands();
    }

    public static void registerCreatorCommands() {
        COMMANDS.add(new Command("SYSTEM.EXIT", (ignored) -> {
            System.exit(0);

            return "0";
        }));

        registerCommonCommands();
    }

    private static void registerCommonCommands() {
    }

    public record Command(String matchString, Function<String[], String> execute) {
        boolean matches(String s) {
            return s.equals(matchString) || (s.split(" ").length > 0 && s.split(" ")[0].equals(matchString));
        }
    }
}
