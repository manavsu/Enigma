package enigma;

import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Manavjot Singh
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _rotated = false;
        _notches = new ArrayList<Integer>();
        for (char i : notches.toCharArray()) {
            _notches.add(perm.alphabet().toInt(i));
        }
    }


    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        if (_notches.contains(wrap(setting() + ring()))) {
            return true;
        }
        return false;
    }

    @Override
    void notchAdvance() {
        _rotated = true;
        set(setting() + 1);
    }

    @Override
    void advance() {
        if (!_rotated) {
            if (_right == null) {
                set(setting() + 1);
                _rotated = true;
            } else {
                if (_right.atNotch()) {
                    set(setting() + 1);
                    _right.notchAdvance();
                    _rotated = true;
                }
            }
        }
        _rotated = false;
    }

    /** Keep Track Of Rotor Notches. */
    private ArrayList<Integer> _notches;

    /** Check if Rotor has been rotated this turn. */
    private Boolean _rotated;

}
