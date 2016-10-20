package org.jglr.jchroma;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.jglr.jchroma.effects.KeyboardEffect;

/**
 * Entry point of the API, allows to create effects for the device and query Razer devices
 */
public class JChroma {

    private static JChroma instance;
    private final ChromaLib wrapper;

    private JChroma() {
        String libName = "RzChromaSDK";
        if(System.getProperty("os.arch").contains("64")) {
            libName += "64";
        }
        wrapper = (ChromaLib) Native.loadLibrary(libName, ChromaLib.class);
    }

    /**
     * Returns the <code>JChroma</code> singleton. One must be warned that this method performs
     * lazy initialisation and that loading the native files is done at initialisation
     * @return
     *          The JChroma singleton
     */
    public static JChroma getInstance() {
        if(instance == null)
            instance = new JChroma();
        return instance;
    }

    private void throwIfError(int err, String method) {
        if(err != 0)
            throw new JChromaException(method, err);
    }

    /**
     * Initialises the ChromaSDK.
     * @throws JChromaException
     *          If there is any error while initialisation
     */
    public void init() {
        int err = wrapper.Init();
        throwIfError(err, "init()");
    }

    /**
     * Frees the ChromaSDK. Cleans up memory and let other applications take control
     * @throws JChromaException
     *          If there is any error while freeing
     */
    public void free() {
        int err = wrapper.UnInit();
        throwIfError(err, "free()");
    }

    /**
     * Creates a keyboard effect on the currently plugged keyboard. The effect is immediately activated when calling this method
     * @param effect
     *          The effect to create
     * @throws JChromaException
     *          If the parameters of the effect are invalid or the effect is not supported
     */
    public void createKeyboardEffect(KeyboardEffect effect) {
        Structure param = effect.createParameter();
        param.write();
        int err = wrapper.CreateKeyboardEffect(effect.getType().ordinal(), param.getPointer(), Pointer.NULL);
        throwIfError(err, "createKeyboardEffect");
    }
}