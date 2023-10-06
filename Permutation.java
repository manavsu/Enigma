package enigma;

import java.util.ArrayList;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Manav
 */
class Permutation {


    /** Keep track of cycles, each node contains the character,
     * a pointer to another node that contains its permuted character,
     * and a pointer to the node of its inverted character.
     */
    static class Node {

        /** Loop through alphabet to set up Nodes first,
         * then do connections.
         * @param c Char from alphabet that is being saved
         */
        Node(char c) {
            _char = c;
            _permute = null;
            _invert = null;
        }

        /** Stores char. */
        private char _char;

        /** Store next. */
        private Node _permute;

        /** Store previous. */
        private Node _invert;
    }

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _derangement = true;
        createPermutationSkeleton();
        cycles = cycles.replaceAll("\\s", "");
        String[] cyclesArr = cycles.split("[)(]");
        for (String cycle : cyclesArr) {
            if (!cycle.equals("")) {
                addCycle(cycle);
            }
        }
        mapIdentites();
    }

    /** Fill in any transforms not stated explicitly. */
    private void mapIdentites() {
        for (Node i : _permutation) {
            if (i._permute == null) {
                i._permute = i;
                i._invert = i;
                if (_derangement) {
                    _derangement = false;
                }
            }
        }
    }

    /** Create skeleton to store permutation. */
    private void createPermutationSkeleton() {
        _permutation = new ArrayList<Node>();
        for (int i = 0; i < size(); i++) {
            _permutation.add(new Node(_alphabet.toChar(i)));
        }
    }

    /** Check if requested c is in _alphabet.
     * @param c get node containing this char.
     * @return return node containing char c*/
    private Node getNode(char c) {
        if (_alphabet.contains(c)) {
            return _permutation.get(_alphabet.toInt(c));
        }
        throw new EnigmaException("Char Not In Alphabet");
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        if (_derangement && cycle.length() == 1) {
            _derangement = false;
        }
        for (int i = 0; i + 1 < cycle.length(); i++) {
            getNode(cycle.charAt(i))._permute = getNode(cycle.charAt(i + 1));
            getNode(cycle.charAt(i + 1))._invert = getNode(cycle.charAt(i));
        }
        getNode(cycle.charAt(cycle.length() - 1))._permute
                = getNode(cycle.charAt(0));
        getNode(cycle.charAt(0))._invert
                = getNode(cycle.charAt(cycle.length() - 1));
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        return _alphabet.toInt(permute(_alphabet.toChar(wrap(p))));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        return _alphabet.toInt(invert(_alphabet.toChar(wrap(c))));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return getNode(p)._permute._char;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return getNode(c)._invert._char;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return _derangement;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Store This Permutation. */
    private ArrayList<Node> _permutation;

    /** Derangement, if nothing maps to itself. */
    private boolean _derangement;
}
