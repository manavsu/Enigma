package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Manavjot Singh
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _right = null;
        _ring = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return the ring position.
     * @return ring
     */
    int ring() {
        return _ring;
    }

    /** Set the ring setting of rotor.
     *
     * @param set ring setting
     */
    void setRing(int set) {
        _ring = set;
    }

    /** Set the ring setting of rotor.
     *
     * @param c ring setting
     */
    void setRing(char c) {
        setRing(_permutation.alphabet().toInt(c));
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _setting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _setting = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        set(_permutation.alphabet().toInt(cposn) - ring());
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        return _permutation.wrap(_permutation.permute(
                p + setting()) - setting());
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        return _permutation.wrap(_permutation.invert(
                e + setting()) - setting());
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    /** Set _right to right rotor.
     * @param rr set right to rr rotor*/
    void right(Rotor rr) {
        _right = rr;
    }

    /** public wrap.
     * @param p  warp p
     * @return p warped*/
    int wrap(int p) {
        return _permutation.wrap(p);
    }

    /** Advance when moved by Notch. */
    void notchAdvance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** Ring. */
    private int _ring;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** Pointer To Right Rotor For Advance. */
    protected Rotor _right;

    /** Keep Track of Rotor Setting. */
    private int _setting;

}
