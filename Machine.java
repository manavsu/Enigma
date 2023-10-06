package enigma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.NoSuchElementException;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Manavjot Singh
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _numPawls = pawls;
        _allRotors = new HashMap<>();
        _rotors = new ArrayList<>();
        for (Rotor rotor : allRotors) {
            _allRotors.put(rotor.name(), rotor);
        }
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _rotors = new ArrayList<Rotor>();
        if (rotors.length != _numRotors) {
            throw new EnigmaException("Wrong Number Of Rotor s Passed");
        }

        for (String key : _allRotors.keySet()) {
            _allRotors.get(key).right(null);
        }

        for (int i = 0; i < numRotors(); i++) {
            Rotor temp = _allRotors.get(rotors[i]);
            if (temp == null) {
                throw new EnigmaException("Rotor "
                        + rotors[i] + " Not Found");
            }
            if (i == 0) {
                if (!temp.reflecting()) {
                    throw new EnigmaException("Rotor "
                            + rotors[i] + " Not A Reflector");
                }
            } else if (i >= numRotors() - numPawls()) {
                if (!temp.rotates()) {
                    throw new EnigmaException("Rotor "
                            + rotors[i] + " Not A Moving Rotor");
                }
            } else {
                if (temp.rotates()) {
                    throw new EnigmaException("Rotor "
                            + rotors[i] + " Not A Moving Rotor");
                }
            }
            if (_rotors.contains(temp)) {
                throw new EnigmaException("Rotor "
                        + rotors[i] + " Cannot Be Repeated");
            }
            _rotors.add(temp);
        }

        for (int x = 0; x < _numRotors - 1; x++) {
            _rotors.get(x).right(_rotors.get(x + 1));
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw new EnigmaException("Setting Length not correct");
        }
        for (int i = 1; i < numRotors(); i++) {
            try {
                _rotors.get(i).set(setting.charAt(i - 1));
            } catch (NoSuchElementException exp) {
                throw new EnigmaException("Setting Does Not Exist");
            }
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugBoard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        for (Rotor rotor : _rotors) {
            rotor.advance();
        }
        c = _plugBoard.permute(c);
        for (int i = numRotors() - 1; i >= 0; i--) {
            c = _rotors.get(i).convertForward(c);
        }

        for (int i = 1; i < numRotors(); i++) {
            c = _rotors.get(i).convertBackward(c);
        }
        return _plugBoard.invert(c);
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String out = "";
        msg = msg.replaceAll("\\s", "");
        for (int i = 0; i < msg.length(); i++) {
            out += _alphabet.toChar(convert(_alphabet.toInt(msg.charAt(i))));
        }
        return out;
    }

    /** Set the Ring Settings.
     * @param setting ring setting
     */
    void setRing(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw new EnigmaException("Ring Setting Length not correct");
        }
        for (int i = 1; i < numRotors(); i++) {
            try {
                _rotors.get(i).setRing(setting.charAt(i - 1));
            } catch (NoSuchElementException exp) {
                throw new EnigmaException("Ring Setting Does Not Exist");
            }
        }
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of Rotors. */
    private int _numRotors;

    /** Number of Moving Rotors. */
    private int _numPawls;

    /** Permutation for plugboard connections. */
    private Permutation _plugBoard;

    /** Store All Rotors. */
    private HashMap<String, Rotor> _allRotors;

    /** Store Rotors In Use. */
    private ArrayList<Rotor> _rotors;

}
