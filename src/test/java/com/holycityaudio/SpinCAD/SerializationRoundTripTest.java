package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests that every CADBlock can survive a serialize/deserialize round-trip,
 * both with and without a control panel open. This catches the class of bugs
 * where non-transient, non-serializable fields (like Swing control panels)
 * break copy/paste/undo.
 */
public class SerializationRoundTripTest {

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    static Stream<String> allBlockClassNames() {
        return BlockDiscovery.findAllBlockClassNames().stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allBlockClassNames")
    void testSerializationRoundTrip(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        Constructor<?> ctor = clazz.getConstructor(int.class, int.class);
        SpinCADBlock block = (SpinCADBlock) ctor.newInstance(100, 100);

        // Round-trip 1: block with no control panel open (baseline)
        byte[] bytes = serialize(block);
        assertNotNull(bytes, "Serialization should produce bytes");
        SpinCADBlock restored = deserialize(bytes);
        assertNotNull(restored, "Deserialization should produce a block");
        assertEquals(block.getName(), restored.getName(), "Name should survive round-trip");
    }

    @ParameterizedTest(name = "{0} with CP")
    @MethodSource("allBlockClassNames")
    void testSerializationWithControlPanel(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        Constructor<?> ctor = clazz.getConstructor(int.class, int.class);
        SpinCADBlock block = (SpinCADBlock) ctor.newInstance(100, 100);

        // Try to open the control panel by calling editBlock()
        // This sets the cp field to a non-null value
        try {
            Method editBlock = clazz.getMethod("editBlock");
            editBlock.invoke(block);
        } catch (Exception e) {
            // Some blocks may not support editBlock in headless mode; that's OK
        }

        // Check if cp field is non-null (i.e., control panel was created)
        boolean cpWasSet = false;
        for (String fieldName : new String[]{"cp", "cP"}) {
            try {
                Field f = clazz.getDeclaredField(fieldName);
                f.setAccessible(true);
                if (f.get(block) != null) {
                    cpWasSet = true;
                    break;
                }
            } catch (NoSuchFieldException ignored) {}
        }

        // Round-trip: should succeed even with cp set
        byte[] bytes = serialize(block);
        assertNotNull(bytes, "Serialization should produce bytes even with control panel open");
        SpinCADBlock restored = deserialize(bytes);
        assertNotNull(restored, "Deserialization should produce a block");
        assertEquals(block.getName(), restored.getName(), "Name should survive round-trip");

        // After round-trip, the cp field should be null (cleared by writeObject or transient)
        for (String fieldName : new String[]{"cp", "cP"}) {
            try {
                Field f = restored.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                assertNull(f.get(restored),
                    "Control panel field '" + fieldName + "' should be null after deserialization");
            } catch (NoSuchFieldException ignored) {}
        }
    }

    @ParameterizedTest(name = "{0} in model")
    @MethodSource("allBlockClassNames")
    void testModelSerializationWithBlock(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        Constructor<?> ctor = clazz.getConstructor(int.class, int.class);
        SpinCADBlock block = (SpinCADBlock) ctor.newInstance(100, 100);

        // Put it in a model (like copy/paste does)
        SpinCADModel model = new SpinCADModel();
        model.addBlock(block);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(model);
        oos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        SpinCADModel restored = (SpinCADModel) ois.readObject();
        ois.close();

        assertEquals(1, restored.blockList.size(), "Model should have 1 block after round-trip");
        assertEquals(block.getName(), restored.blockList.get(0).getName());
    }

    private byte[] serialize(SpinCADBlock block) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(block);
        oos.close();
        return baos.toByteArray();
    }

    private SpinCADBlock deserialize(byte[] bytes) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        SpinCADBlock block = (SpinCADBlock) ois.readObject();
        ois.close();
        return block;
    }
}
