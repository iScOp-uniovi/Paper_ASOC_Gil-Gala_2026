package evrp_0._experiments;

import evrp_0.algorithms.ensembles.Ensemble;
import evrp_0.util.AlgoUtil;

/**
 * Implements the SEC algorithm for evolving ensembles of priority rules.
 *
 * <p>The algorithm repeatedly generates random ensembles and retains the best
 * one according to the training criterion. The current best ensemble can then
 * be improved by mutating its constituent rules.</p>
 *
 * <p>The execution mode and optimization criterion are fixed for this
 * experiment:</p>
 *
 * <ul>
 *   <li>{@code mode = 1}</li>
 *   <li>{@code criterio = 3}</li>
 * </ul>
 *
 * <p>The combination method is configurable from the command line:</p>
 *
 * <ul>
 *   <li>{@code 0}: sum</li>
 *   <li>{@code 1}: vote</li>
 * </ul>
 *
 * <p>Command-line syntax:</p>
 *
 * <pre>
 * java evrp_0._experiments.SEC sizeEnsembles modeEnsembles combinationMethod
 * </pre>
 *
 * @author Francisco Javier Gil-Gala
 */
public class SEC extends MGA {

    /** Combination method based on summing rule outputs. */
    private static final int COMBINATION_SUM = 0;

    /** Combination method based on voting. */
    private static final int COMBINATION_VOTE = 1;

    /** Default ensemble size. */
    private static final int DEFAULT_ENSEMBLE_SIZE = 5;

    /** Default ensemble mode. */
    private static final int DEFAULT_ENSEMBLE_MODE = 0;

    /** Default rule-combination method. */
    private static final int DEFAULT_COMBINATION_METHOD = COMBINATION_SUM;

    /**
     * Entry point of the SEC experiment.
     *
     * <p>If no arguments are provided, the following default configuration is
     * used:</p>
     *
     * <pre>
     * sizeEnsembles     = 5
     * modeEnsembles     = 0
     * combinationMethod = 0 (sum)
     * </pre>
     *
     * <p>If arguments are provided, exactly three values are required:</p>
     *
     * <ol>
     *   <li>ensemble size,</li>
     *   <li>ensemble mode,</li>
     *   <li>combination method: {@code 0 = sum}, {@code 1 = vote}.</li>
     * </ol>
     *
     * @param args command-line arguments
     * @throws IllegalArgumentException if the number of arguments is invalid
     *                                  or the combination method is not 0 or 1
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            args = new String[] {
                    String.valueOf(DEFAULT_ENSEMBLE_SIZE),
                    String.valueOf(DEFAULT_ENSEMBLE_MODE),
                    String.valueOf(DEFAULT_COMBINATION_METHOD)
            };
        } else if (args.length != 3) {
            throw new IllegalArgumentException(
                    "SEC expects exactly 3 arguments: "
                    + "sizeEnsembles modeEnsembles combinationMethod");
        }

        AlgoUtil.sizeEnsembles = Integer.parseInt(args[0]);
        AlgoUtil.modeEnsembles = Integer.parseInt(args[1]);
        AlgoUtil.combinationMethod = Integer.parseInt(args[2]);

        validateCombinationMethod(AlgoUtil.combinationMethod);

        /*
         * Global execution settings.
         */
        AlgoUtil.minutos = 100;
        AlgoUtil.intervalosMedicion = 100;

        /*
         * Experiment identifier used by the framework to name output files
         * and logs.
         */
        String msg = "SEC_" + AlgoUtil.getInfoMode() + "_"
                + AlgoUtil.getCriterio() + "_"
                + AlgoUtil.sizeEnsembles + "_"
                + AlgoUtil.modeEnsembles + "_"
                + AlgoUtil.combinationMethod;

        SEC sec = new SEC();

        /*
         * SEC does not use surrogate evaluation in this configuration.
         */
        AlgoUtil.V2 = false;

        /*
         * Local-search configuration inherited from MGA.
         */
        sec.ratioMutacionReglas = 1.0;
        sec.nInstanceFilter = 3;
        sec.nMutations = 10;

        AlgoUtil.run(msg, sec);

        /*
         * Load the rules that can be used to construct and modify ensembles.
         * The file reglas_ensembles_lex1.csv must be available in the current
         * working directory.
         */
        loadRules("reglas_ensembles_lex1", 0);

        sec.run();

        AlgoUtil.end();
    }

    /**
     * Checks that the selected rule-combination method is supported.
     *
     * @param combinationMethod combination method identifier
     * @throws IllegalArgumentException if the value is different from
     *                                  {@code 0} (sum) and {@code 1} (vote)
     */
    private static void validateCombinationMethod(int combinationMethod) {
        if (combinationMethod != COMBINATION_SUM
                && combinationMethod != COMBINATION_VOTE) {
            throw new IllegalArgumentException(
                    "Invalid combinationMethod: " + combinationMethod
                    + ". Valid values are 0 (sum) and 1 (vote).");
        }
    }

    /**
     * Executes the SEC search until the global time limit is reached.
     *
     * <p>At each iteration, a random ensemble is generated. If it improves the
     * current best solution on the training set, it becomes the new incumbent.
     * Afterwards, the incumbent is subjected to rule-level mutation.</p>
     *
     * <p>When {@link #nInstanceFilter} is greater than zero, candidate
     * mutations are first evaluated on a randomly selected subset of training
     * instances. The best candidate according to this filter is then evaluated
     * on the complete training set before it can replace the incumbent.</p>
     */
    @Override
    public void run() {
        Ensemble best = null;

        while (!AlgoUtil.timeOut()) {
            /*
             * Generate a new random ensemble and keep it if it improves the
             * current best solution.
             */
            Ensemble candidate = new Ensemble().random();

            if (best == null || AlgoUtil.mejoraTraining(candidate, best)) {
                best = candidate;
            }

            generations++;

            /*
             * Apply rule-level mutation/local search according to the
             * configured probability.
             */
            if (Math.random() <= ratioMutacionReglas) {
                if (nInstanceFilter > 0) {
                    /*
                     * Surrogate/filter stage based on a subset of the training
                     * instances.
                     */
                    AlgoUtil.set_filter = getInstancias(nInstanceFilter);
                    best.evalFilter = false;

                    Ensemble bestLocal = null;

                    for (int i = 0; i < nMutations; i++) {
                        Ensemble mutation = mutar3(best);

                        if (bestLocal == null
                                || mutation.filter() < bestLocal.filter()) {
                            bestLocal = mutation;
                        }
                    }

                    /*
                     * A candidate selected by the filter is accepted only if
                     * it also improves the complete training objective.
                     */
                    if (bestLocal.train() < best.train()) {
                        bestLocal.evalFilter = false;
                        best = bestLocal;
                    }

                } else {
                    /*
                     * Full-evaluation local search without instance filtering.
                     */
                    for (int i = 0; i < nMutations; i++) {
                        Ensemble mutation = mutarReglas(best);

                        if (mutation.train() < best.train()) {
                            best = mutation;
                            break;
                        }
                    }
                }
            }
        }
    }
}