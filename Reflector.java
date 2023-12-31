package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a reflector in the enigma.
 *  @author Manavjot Singh
 */
class Reflector extends FixedRotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM. */
    Reflector(String name, Permutation perm) {
        super(name, perm);
    }

    @Override
    boolean reflecting() {
        return true;
    }

    @Override
    int setting() {
        return 0;
    }

    @Override
    void set(int posn) {
        if (posn != 0) {
            throw error("reflector has only one position");
        }
    }

    @Override
    void set(char cposn) {
        set(permutation().alphabet().toInt(cposn));
    }
}
