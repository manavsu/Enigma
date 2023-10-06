package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;


import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;


import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Manavjot Singh
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine A = readConfig();
        String setup = _input.nextLine(), coded, buffer;
        while (setup.equals("")) {
            setup = _input.nextLine();
            printMessageLine("");
        }
        setUp(A, setup);

        while (_input.hasNext()) {
            if (_input.hasNext("[*]")) {
                setup = _input.nextLine();
                while (setup.equals("")) {
                    setup = _input.nextLine();
                    printMessageLine("");
                }
                setUp(A, setup);
            } else {
                coded = _input.nextLine();
                printMessageLine(A.convert(coded));
            }
        }
        _input.useDelimiter("");
        while (_input.hasNext("[\\n]")) {
            _input.next();
            printMessageLine("");
        }
        _input.close();
        _output.close();
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            int numRotors, pawls;
            ArrayList<Rotor> allRotors = new ArrayList<>();

            _config.hasNext();
            readAlphabet(_config.next());

            _config.hasNextInt();
            numRotors = _config.nextInt();
            _config.hasNextInt();
            pawls = _config.nextInt();
            if ((numRotors <= pawls) || (pawls < 0)) {
                throw new EnigmaException("Config File Read Fail");
            }

            while (_config.hasNext("[^()]*")) {
                allRotors.add(readRotor());

            }
            _config.close();

            return new Machine(_alphabet, numRotors, pawls, allRotors);

        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }


    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = null, type = null, cycles = "";
            name = _config.next();
            if (!_config.hasNext("[RNM][^()*]*")) {
                throw new EnigmaException("Bad Rotor Description");
            }
            type = _config.next();

            while (_config.hasNext("[(].*[)]")) {
                cycles = cycles + " " + _config.next();
            }

            if (type.charAt(0) == 'M') {
                return new MovingRotor(name, new Permutation(cycles, _alphabet),
                        type.substring(1));
            } else if (type.charAt(0) == 'N') {
                return new FixedRotor(name, new Permutation(cycles, _alphabet));
            } else if (type.charAt(0) == 'R') {
                return new Reflector(name, new Permutation(cycles, _alphabet));
            } else {
                throw new EnigmaException("Bad Rotor Description");
            }
        } catch (NoSuchElementException excp) {
            throw error("Bad Rotor Description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        try {
            String[] rotors = new String[M.numRotors()];
            String set, perm = "", ring = "";
            Scanner setting = new Scanner(settings);
            if (!setting.hasNext("[*]")) {
                throw new EnigmaException("Bad Setting Description");
            }
            setting.next();

            for (int x = 0; x < M.numRotors(); x++) {
                rotors[x] = setting.next();
            }

            set = setting.next();
            M.insertRotors(rotors);


            if (setting.hasNext("[^()]*")) {
                ring = setting.next();
                M.setRing(ring);
            }

            M.setRotors(set);

            while (setting.hasNext("[(].*[)]")) {
                perm = perm +  " " + setting.next();
            }

            if (setting.hasNext()) {
                throw new EnigmaException("Setup Failure");
            }

            M.setPlugboard(new Permutation(perm, _alphabet));

        } catch (NoSuchElementException excp) {
            throw new EnigmaException("Setup Failure");
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int x = 0; x < msg.length(); x++) {
            _output.print(msg.charAt(x));
            if ((x + 1) % 5 == 0 && x != msg.length() - 1) {
                _output.print(' ');
            }
        }
        _output.print('\n');
    }

    /** Check for Invalid Characters in alphabet.
     * @param alphabet String containing alphabet */
    private void readAlphabet(String alphabet) {
        if (alphabet.contains("(") || alphabet.contains(")")
                || alphabet.contains("*")) {
            throw new  EnigmaException("Config File Invalid");
        }
        _alphabet = new Alphabet(alphabet);
    }


    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
