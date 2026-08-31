# MGA: Memetic Genetic Algorithm for Rule Ensembles

`MGA` is a memetic genetic algorithm that evolves ensembles of priority rules. It extends the base `GA` implementation with a rule-level local search that can evaluate candidate changes either on the complete training set or on a randomly sampled subset of training instances.

The executable class is:

```text
evrp_0._experiments.MGA
```

## Fixed configuration

The execution time is also currently fixed to:

```text
100 minutes
```

and the experimental framework uses:

```text
100 measurement intervals
```

## Required rule file

The algorithm loads the rules used to build ensembles from:

```text
reglas_ensembles_lex1.csv
```

The file must be available from the working directory used to launch the Java program, unless the project changes how relative paths are resolved.

## Command-line syntax

When a custom configuration is supplied, the program expects exactly **9 arguments**:

```text
java -cp <classpath> evrp_0._experiments.MGA \
  <sizeEnsembles> \
  <modeEnsembles> \
  <combinationMethod> \
  <popSize> \
  <crossoverProbability> \
  <mutationProbability> \
  <mutationRulesProbability> \
  <nMutations> \
  <nInstanceFilter>
```

On Windows `cmd.exe`, the same command can be written on one line:

```bat
java -cp "bin" evrp_0._experiments.MGA 3 0 0 100 80 20 50 5 0
```

If the project requires external JAR files stored in `lib`, a typical Windows classpath is:

```bat
java -cp "bin;lib/*" evrp_0._experiments.MGA 3 0 0 100 80 20 50 5 0
```

On Linux/macOS the classpath separator is `:` instead of `;`:

```bash
java -cp "bin:lib/*" evrp_0._experiments.MGA 3 0 0 100 80 20 50 5 0
```

## Parameters

| Position | Parameter | Default | Description |
|---:|---|---:|---|
| 1 | `sizeEnsembles` | `3` | Number of rules in each ensemble. |
| 2 | `modeEnsembles` | `0` | Ensemble mode used by the existing `AlgoUtil`/`Ensemble` implementation. |
| 3 | `combinationMethod` | `0` | Method used to combine the rules: `0 = sum`, `1 = vote`. |
| 4 | `popSize` | `100` | Population size. |
| 5 | `crossoverProbability` | `80` | Crossover probability as an integer percentage. `80` means 0.80. |
| 6 | `mutationProbability` | `20` | Ensemble-level mutation probability as an integer percentage. `20` means 0.20. |
| 7 | `mutationRulesProbability` | `50` | Probability of applying rule-level local search to an offspring. `0` disables this component and yields the GA behavior. |
| 8 | `nMutations` | `5` | Number of candidate rule mutations evaluated in each local-search iteration. |
| 9 | `nInstanceFilter` | `0` | Number of randomly selected training instances used to filter local-search candidates. `0` evaluates local search directly on the complete training set. |

## Combination methods

Two combination methods are accepted:

```text
0 = sum
1 = vote
```

Any other value causes the program to stop with an `IllegalArgumentException`.

### Sum

Example:

```bat
java -cp "bin" evrp_0._experiments.MGA 3 0 0 100 80 20 50 5 0
```

### Vote

Example:

```bat
java -cp "bin" evrp_0._experiments.MGA 3 0 1 100 80 20 50 5 0
```

The only difference between these two examples is `combinationMethod`.

## Running with the default configuration

If no arguments are provided, `MGA` uses the following configuration:

```text
sizeEnsembles            = 3
modeEnsembles            = 0
combinationMethod        = 0  (sum)
popSize                  = 100
crossoverProbability     = 80
mutationProbability      = 20
mutationRulesProbability = 50
nMutations               = 5
nInstanceFilter          = 0
```

Run it with:

```bat
java -cp "bin" evrp_0._experiments.MGA
```

## Local-search behavior

The memetic component is controlled mainly by `mutationRulesProbability`, `nMutations`, and `nInstanceFilter`.

For each application of the rule-level local search:

1. One rule of the ensemble is selected and replaced by a mutated rule to create a neighboring ensemble.
2. `nMutations` neighboring ensembles are generated in each local-search iteration.
3. The best candidate is identified.
4. The candidate replaces the current ensemble only when it improves the training objective.
5. The process continues until no improving move is found.

When `nInstanceFilter > 0`, candidate neighbors are first compared using a randomly selected subset of the training instances. The candidate selected by this filter is then checked against the current solution using the complete training objective.

When `nInstanceFilter = 0`, every candidate is evaluated directly using the complete training objective.

## Pure GA configuration

To disable the rule-level local search, set:

```text
mutationRulesProbability = 0
```

For example:

```bat
java -cp "bin" evrp_0._experiments.MGA 3 0 0 100 80 20 0 5 0
```

## Example with filtered local search

The following configuration applies rule-level local search with probability 50%, generates 5 candidate mutations per local-search iteration, and uses 20 randomly selected training instances for candidate filtering:

```bat
java -cp "bin" evrp_0._experiments.MGA 3 0 0 100 80 20 50 5 20
```

## Compiling the JavaDoc

Once the complete project is available on the classpath/source path, the documentation for this class can be generated with the standard `javadoc` tool. For example, if the source root is `src` and compiled dependencies are in `bin`:

```bat
javadoc -d docs -classpath "bin" -sourcepath "src" evrp_0._experiments.MGA
```

If the project uses external dependencies, add them to the classpath as required.

## Notes

`MGA.java` depends on the existing project classes `GA`, `Ensemble`, `Population`, `Regla`, `Instance`, and `AlgoUtil`. Therefore, this source file is not intended to be compiled as a standalone Java file outside the original project.

The meaning of `modeEnsembles` is defined by the existing project implementation and is intentionally left unchanged here. The command-line interface only removes `mode` and `criterion`, which are now fixed internally, and explicitly validates the two supported combination methods.
